package api2tool

import (
	"encoding/json"
	"fmt"
	"io/fs"
	"iter"
	"maps"
	"os"
	"path/filepath"
	"reflect"
	"slices"
	"strings"
	"testing"

	"github.com/pb33f/libopenapi/datamodel/high/base"
	v3 "github.com/pb33f/libopenapi/datamodel/high/v3"
	"sigs.k8s.io/yaml"
)

// The module specs embedded by the apispec package.
const modulesDir = "../../apispec/modules"

// TestSchemasRoundTripThroughJSONSchema checks that inline keeps every schema of the
// module specs as libopenapi renders it, although it goes through jsonschema.Schema,
// so clients see the upstream schemas unchanged.
func TestSchemasRoundTripThroughJSONSchema(t *testing.T) {
	var paths []string
	err := filepath.WalkDir(modulesDir, func(path string, d fs.DirEntry, err error) error {
		if err == nil && !d.IsDir() && strings.HasSuffix(path, ".yaml") {
			paths = append(paths, path)
		}
		return err
	})
	if err != nil {
		t.Fatal(err)
	}
	if len(paths) == 0 {
		t.Fatalf("no module specs in %s", modulesDir)
	}

	for _, path := range paths {
		t.Run(filepath.Base(filepath.Dir(path)), func(t *testing.T) {
			raw, err := os.ReadFile(path)
			if err != nil {
				t.Fatal(err)
			}
			spec, err := Parse(raw)
			if err != nil {
				t.Fatal(err)
			}
			for name, proxy := range specSchemas(spec) {
				schema, err := inline(proxy)
				if err != nil && strings.Contains(err.Error(), "recursive schema") {
					// Build rejects these, they never reach a client.
					continue
				}
				if err != nil {
					t.Fatalf("%s: %v", name, err)
				}
				want := rendered(t, proxy)
				var got map[string]any
				roundTrip(t, schema, &got)
				for _, d := range diff("", want, got) {
					t.Errorf("%s changed in the round trip: %s", name, d)
				}
			}
		})
	}
}

// specSchemas yields the component schemas of a spec and the schemas of its
// parameters and JSON request bodies, which may be defined inline.
func specSchemas(spec *v3.Document) iter.Seq2[string, *base.SchemaProxy] {
	return func(yield func(string, *base.SchemaProxy) bool) {
		if spec.Components != nil && spec.Components.Schemas != nil {
			for name, proxy := range spec.Components.Schemas.FromOldest() {
				if !yield("components.schemas."+name, proxy) {
					return
				}
			}
		}
		if spec.Paths == nil {
			return
		}
		for path, item := range spec.Paths.PathItems.FromOldest() {
			for _, p := range item.Parameters {
				if p.Schema != nil && !yield(path+" parameter "+p.Name, p.Schema) {
					return
				}
			}
			for method, op := range item.GetOperations().FromOldest() {
				for _, p := range op.Parameters {
					if p.Schema != nil && !yield(method+" "+path+" parameter "+p.Name, p.Schema) {
						return
					}
				}
				if op.RequestBody == nil || op.RequestBody.Content == nil {
					continue
				}
				if media, ok := op.RequestBody.Content.Get("application/json"); ok && media.Schema != nil {
					if !yield(method+" "+path+" request body", media.Schema) {
						return
					}
				}
			}
		}
	}
}

// diff returns the places where got differs from want.
func diff(path string, want, got any) []string {
	wantMap, wantOK := want.(map[string]any)
	gotMap, gotOK := got.(map[string]any)
	if wantOK && gotOK {
		var diffs []string
		for _, k := range slices.Sorted(maps.Keys(wantMap)) {
			if _, ok := gotMap[k]; !ok {
				diffs = append(diffs, fmt.Sprintf("%s/%s: %v was dropped", path, k, wantMap[k]))
				continue
			}
			diffs = append(diffs, diff(path+"/"+k, wantMap[k], gotMap[k])...)
		}
		for _, k := range slices.Sorted(maps.Keys(gotMap)) {
			if _, ok := wantMap[k]; !ok {
				diffs = append(diffs, fmt.Sprintf("%s/%s: %v was added", path, k, gotMap[k]))
			}
		}
		return diffs
	}
	wantList, wantOK := want.([]any)
	gotList, gotOK := got.([]any)
	if wantOK && gotOK && len(wantList) == len(gotList) {
		var diffs []string
		for i := range wantList {
			diffs = append(diffs, diff(fmt.Sprintf("%s/%d", path, i), wantList[i], gotList[i])...)
		}
		return diffs
	}
	if !reflect.DeepEqual(want, got) {
		return []string{fmt.Sprintf("%s: want %#v, got %#v", path, want, got)}
	}
	return nil
}

// rendered returns the schema as libopenapi renders it, with all references resolved.
func rendered(t *testing.T, proxy *base.SchemaProxy) map[string]any {
	t.Helper()
	schema, err := proxy.BuildSchema()
	if err != nil {
		t.Fatal(err)
	}
	raw, err := schema.RenderInline()
	if err != nil {
		t.Fatal(err)
	}
	var out map[string]any
	if err := yaml.Unmarshal(raw, &out); err != nil {
		t.Fatal(err)
	}
	return out
}

// roundTrip converts from into to through JSON.
func roundTrip(t *testing.T, from, to any) {
	t.Helper()
	raw, err := json.Marshal(from)
	if err != nil {
		t.Fatal(err)
	}
	if err := json.Unmarshal(raw, to); err != nil {
		t.Fatal(err)
	}
}
