package toolkit

import (
	"errors"
	"testing"

	"github.com/modelcontextprotocol/go-sdk/mcp"
)

var formCaps = &mcp.ClientCapabilities{Elicitation: &mcp.ElicitationCapabilities{Form: &mcp.FormElicitationCapabilities{}}}

func accepted() *mcp.ElicitResult {
	return &mcp.ElicitResult{Action: "accept", Content: map[string]any{"confirm": true}}
}

// confirmRequestKey returns the key of the single input request of res.
func confirmRequestKey(t *testing.T, res *mcp.CallToolResult) string {
	t.Helper()
	if res == nil || len(res.InputRequests) != 1 {
		t.Fatalf("result = %+v, want a single input request", res)
	}
	for key := range res.InputRequests {
		return key
	}
	return ""
}

func TestConfirmGoesAheadOnceTheUserAgrees(t *testing.T) {
	const action = "Delete project A"
	res, err := confirm(nil, formCaps, action)
	if err != nil {
		t.Fatalf("first call: %v", err)
	}
	key := confirmRequestKey(t, res)

	res, err = confirm(mcp.InputResponseMap{key: accepted()}, formCaps, action)
	if res != nil || err != nil {
		t.Errorf("retry = %+v, %v, want nil, nil", res, err)
	}
}

func TestConfirmRefusesWhenTheUserDeclines(t *testing.T) {
	const action = "Delete project A"
	key := confirmRequestKey(t, must(confirm(nil, formCaps, action)))
	for name, answer := range map[string]*mcp.ElicitResult{
		"declined":  {Action: "decline"},
		"cancelled": {Action: "cancel"},
		"unchecked": {Action: "accept", Content: map[string]any{"confirm": false}},
	} {
		t.Run(name, func(t *testing.T) {
			_, err := confirm(mcp.InputResponseMap{key: answer}, formCaps, action)
			var toolErr *Error
			if !errors.As(err, &toolErr) || toolErr.Outcome != OutcomeDeclined {
				t.Errorf("err = %v, want a declined tool error", err)
			}
		})
	}
}

// TestConfirmAsksAgainWhenTheActionChanges covers a client retrying the call with different
// arguments but the responses to the first call: the earlier answer must not confirm the new action.
func TestConfirmAsksAgainWhenTheActionChanges(t *testing.T) {
	oldKey := confirmRequestKey(t, must(confirm(nil, formCaps, "Delete project A")))

	res, err := confirm(mcp.InputResponseMap{oldKey: accepted()}, formCaps, "Delete project B")
	if err != nil {
		t.Fatalf("retry with another action: %v", err)
	}
	if newKey := confirmRequestKey(t, res); newKey == oldKey {
		t.Errorf("the new action is asked under the old key %q", oldKey)
	}
}

func TestConfirmGoesAheadForClientsWithoutFormElicitation(t *testing.T) {
	for name, caps := range map[string]*mcp.ClientCapabilities{
		"no capabilities": nil,
		"no elicitation":  {},
		"url elicitation": {Elicitation: &mcp.ElicitationCapabilities{URL: &mcp.URLElicitationCapabilities{}}},
	} {
		t.Run(name, func(t *testing.T) {
			if res, err := confirm(nil, caps, "Delete project A"); res != nil || err != nil {
				t.Errorf("confirm = %+v, %v, want nil, nil", res, err)
			}
		})
	}
}

func must(res *mcp.CallToolResult, err error) *mcp.CallToolResult {
	if err != nil {
		panic(err)
	}
	return res
}
