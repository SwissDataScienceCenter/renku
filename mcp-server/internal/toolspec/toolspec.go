// Package toolspec builds the MCP tools of the operations allowlisted in apispec.Tools
// from the renku-data-services API specs, and the API request of a tool call.
//
// The input schema of a tool combines the parameters and the request body of the
// operation, with the upstream schemas passed to clients as they are. The arguments
// are not validated against it, the Renku API validates the requests it receives.
// The arguments of a tool are its path and query parameters plus the request
// body as the body argument.
package toolspec

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"slices"
	"strconv"
	"strings"
)

// BodyArgument is the tool argument that holds the request body.
const BodyArgument = "body"

// Tool is one API operation exposed as an MCP tool.
type Tool struct {
	Name        string
	Description string
	// InputSchema is the JSON Schema of the arguments, advertised to clients.
	InputSchema map[string]any

	// Method is the uppercase HTTP method of the operation.
	Method string
	// Path is the path template of the operation, relative to the API base URL.
	Path            string
	PathParameters  []string
	QueryParameters []string
	// Body is whether the operation takes a JSON request body.
	Body bool
}

// Request builds the API request of a call. baseURL is the URL the paths
// are relative to, e.g. http://renku-gateway/api/data. It only fails for
// arguments that cannot be put into a request, all other checks are left to the API.
func (t Tool) Request(ctx context.Context, baseURL string, args map[string]any) (*http.Request, error) {
	// Unknown arguments would otherwise be dropped silently, so the model would not notice misspelled ones.
	for name := range args {
		if !slices.Contains(t.PathParameters, name) && !slices.Contains(t.QueryParameters, name) && (name != BodyArgument || !t.Body) {
			return nil, fmt.Errorf("unknown argument %s", name)
		}
	}
	path := t.Path
	for _, name := range t.PathParameters {
		if args[name] == nil {
			return nil, fmt.Errorf("missing required argument %s", name)
		}
		value, err := scalar(args[name])
		if err != nil {
			return nil, fmt.Errorf("%s: %w", name, err)
		}
		path = strings.ReplaceAll(path, "{"+name+"}", url.PathEscape(value))
	}
	query := url.Values{}
	for _, name := range t.QueryParameters {
		values, ok := args[name].([]any)
		if !ok {
			values = []any{args[name]}
		}
		for _, v := range values {
			// Absent and null arguments are left out, so the API applies its defaults.
			if v == nil {
				continue
			}
			s, err := scalar(v)
			if err != nil {
				return nil, fmt.Errorf("%s: %w", name, err)
			}
			query.Add(name, s)
		}
	}
	target := strings.TrimSuffix(baseURL, "/") + path
	if len(query) > 0 {
		target += "?" + query.Encode()
	}

	var body io.Reader
	if value, ok := args[BodyArgument]; t.Body && ok {
		raw, err := json.Marshal(value)
		if err != nil {
			return nil, fmt.Errorf("%s: %w", BodyArgument, err)
		}
		body = bytes.NewReader(raw)
	}
	req, err := http.NewRequestWithContext(ctx, t.Method, target, body)
	if err != nil {
		return nil, err
	}
	if body != nil {
		req.Header.Set("Content-Type", "application/json")
	}
	return req, nil
}

// scalar formats a JSON scalar as a parameter value.
func scalar(v any) (string, error) {
	switch v := v.(type) {
	case string:
		return v, nil
	case bool:
		return strconv.FormatBool(v), nil
	case float64:
		// 'f' instead of fmt's default, which formats large numbers like 1e+06.
		return strconv.FormatFloat(v, 'f', -1, 64), nil
	case json.Number:
		return v.String(), nil
	}
	return "", fmt.Errorf("expected a string, number or boolean, got %T", v)
}
