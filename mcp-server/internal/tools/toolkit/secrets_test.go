package toolkit

import (
	"encoding/json"
	"os"
	"slices"
	"strings"
	"testing"
)

func TestSecretKeysFindsCredentials(t *testing.T) {
	storage := map[string]any{
		"configuration": map[string]any{
			"type":              "s3",
			"provider":          "AWS",
			"endpoint":          "https://s3.example.org",
			"access_key_id":     "AKIA123",
			"secret_access_key": "",   // empty: filled in later by the user
			"ask_password":      true, // a flag, not a secret
			"my_api_token":      "t0k3n",
			"token_url":         "https://auth.example.org/token",
			"url":               "https://user:hunter2@dav.example.org/remote.php",
			"nested":            []any{map[string]any{"Password": "x"}},
		},
		"source_path": "bucket/data",
	}
	got := SecretKeys(storage)
	want := []string{
		"configuration.access_key_id",
		"configuration.my_api_token",
		"configuration.nested[0].Password",
		"configuration.url",
	}
	if !slices.Equal(got, want) {
		t.Errorf("SecretKeys = %q, want %q", got, want)
	}
	if err := RejectSecrets(storage, "storage"); err == nil || !strings.Contains(err.Error(), "configuration.access_key_id") {
		t.Errorf("RejectSecrets = %v", err)
	}
	if err := RejectSecrets(map[string]any{"configuration": map[string]any{"type": "webdav", "url": "https://dav.example.org"}}, "storage"); err != nil {
		t.Errorf("RejectSecrets refused a configuration without credentials: %v", err)
	}
}

func TestRedactSecretsKeepsEverythingElse(t *testing.T) {
	in := map[string]any{"name": "data", "storage": map[string]any{"configuration": map[string]any{"type": "s3", "secret_access_key": "s3cr3t"}}}
	out, _ := json.Marshal(RedactSecrets(in))
	if want := `{"name":"data","storage":{"configuration":{"secret_access_key":"[redacted]","type":"s3"}}}`; string(out) != want {
		t.Errorf("RedactSecrets = %s, want %s", out, want)
	}
}

// TestRedactSecretsRedactsWhatSecretKeysFinds checks that the values refused by RejectSecrets are
// exactly the ones redacted from the logs.
func TestRedactSecretsRedactsWhatSecretKeysFinds(t *testing.T) {
	in := map[string]any{
		"type":         "webdav",
		"url":          "https://user:hunter2@dav.example.org",
		"ask_password": true,
		"pass":         "",
		"nested":       []any{map[string]any{"Password": "x", "host": "h"}},
	}
	out, _ := json.Marshal(RedactSecrets(in))
	want := `{"ask_password":true,"nested":[{"Password":"[redacted]","host":"h"}],"pass":"","type":"webdav","url":"[redacted]"}`
	if string(out) != want {
		t.Errorf("RedactSecrets = %s, want %s", out, want)
	}
	if got, want := SecretKeys(in), []string{"nested[0].Password", "url"}; !slices.Equal(got, want) {
		t.Errorf("SecretKeys = %q, want %q", got, want)
	}
}

// notSecret are options rclone marks Sensitive that name a resource rather than carry a credential.
// Renku itself unmarks some of them (account, host, user for some providers, see rclone_patches.py
// in renku-data-services). Refusing them would refuse ordinary connectors, e.g. any WebDAV or SFTP one.
var notSecret = []string{
	"account", "account_id", "api_url", "app_id", "cloud_name", "compartment", "deleted_id", "device_id",
	"domain", "host", "namenode", "namespace", "project_number", "public_key", "root_folder_slug", "root_id",
	"service_principal_name", "spn", "tenant_domain", "tenant_id", "url", "user_project",
}

// TestSecretKeysCoverTheRcloneSchema keeps secretOptions in sync with the rclone schema, which
// fetchspecs copies from renku-data-services. When it fails, add the option to secretOptions if it
// carries a credential, or to notSecret if it does not.
func TestSecretKeysCoverTheRcloneSchema(t *testing.T) {
	raw, err := os.ReadFile("../../renkuapi/rclone_schema.json")
	if err != nil {
		t.Fatal(err)
	}
	var providers []struct {
		Prefix  string
		Options []struct {
			Name                  string
			IsPassword, Sensitive bool
		}
	}
	if err := json.Unmarshal(raw, &providers); err != nil {
		t.Fatal(err)
	}
	if len(providers) == 0 {
		t.Fatal("no providers in the rclone schema")
	}
	for _, p := range providers {
		for _, o := range p.Options {
			if !o.IsPassword && !o.Sensitive {
				continue
			}
			caught := isSecretKey(o.Name)
			if o.IsPassword && !caught {
				t.Errorf("password option %s of %s is not refused", o.Name, p.Prefix)
			}
			if !caught && !slices.Contains(notSecret, o.Name) {
				t.Errorf("sensitive option %s of %s is neither refused nor listed in notSecret", o.Name, p.Prefix)
			}
		}
	}
	for _, name := range notSecret {
		if isSecretKey(name) {
			t.Errorf("%s is listed in notSecret but refused", name)
		}
	}
}
