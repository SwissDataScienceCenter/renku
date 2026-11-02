package toolkit

import (
	"crypto/sha256"
	"encoding/hex"

	"github.com/modelcontextprotocol/go-sdk/mcp"
)

// OutcomeDeclined is the logged outcome of a call the user did not confirm.
const OutcomeDeclined = "declined"

// confirmKey is the key of the confirmation of action in the input requests and responses of a call.
// It is derived from action, which names what the call changes, so that an answer given for one
// action is not taken for another: if the client retries the call with different arguments but the
// old responses, the key does not match and the user is asked again.
func confirmKey(action string) string {
	sum := sha256.Sum256([]byte(action))
	return "confirm-" + hex.EncodeToString(sum[:8])
}

var confirmSchema = map[string]any{
	"type": "object",
	"properties": map[string]any{
		"confirm": map[string]any{"type": "boolean", "description": "Confirm that this should go ahead."},
	},
	"required": []string{"confirm"},
}

// Confirm asks the user to agree to action, e.g. "Delete session launcher X", before a tool changes
// something that cannot be undone. Use it at the start of the handler:
//
//	if res, err := toolkit.Confirm(req, action); res != nil || err != nil {
//		return res, nil, err
//	}
//
// The first call returns a result asking the client to elicit the confirmation, and the client calls
// the tool again with the answer. This needs no session: clients on protocol 2026-07-28 retry the call
// themselves, and for older clients over stdio the SDK asks them and calls the handler again. Confirm
// returns an error if the user did not agree, and nil, nil once they did.
//
// Clients that do not declare form elicitation, which includes older clients over stateless HTTP,
// are not asked, and the call goes ahead. They rely on the tool's DestructiveHint to ask the user, and
// the description tells the model to. The confirmation is a usability feature, not a security check:
// the client sends the answer.
func Confirm(req *mcp.CallToolRequest, action string) (*mcp.CallToolResult, error) {
	return confirm(req.Params.InputResponses, req.ClientCapabilities(), action)
}

func confirm(responses mcp.InputResponseMap, caps *mcp.ClientCapabilities, action string) (*mcp.CallToolResult, error) {
	key := confirmKey(action)
	if answer, ok := responses[key]; ok {
		if r, ok := answer.(*mcp.ElicitResult); ok && r.Action == "accept" && r.Content["confirm"] == true {
			return nil, nil
		}
		return nil, &Error{Message: "The user did not agree to this: " + action + ". Do not retry it.", Outcome: OutcomeDeclined}
	}
	if !canElicitForm(caps) {
		return nil, nil
	}
	return &mcp.CallToolResult{InputRequests: mcp.InputRequestMap{
		key: &mcp.ElicitParams{Mode: "form", Message: action + ". Go ahead?", RequestedSchema: confirmSchema},
	}}, nil
}

// canElicitForm reports whether a client with caps supports form elicitation. An elicitation
// capability without modes means form only.
func canElicitForm(caps *mcp.ClientCapabilities) bool {
	if caps == nil || caps.Elicitation == nil {
		return false
	}
	e := caps.Elicitation
	return e.Form != nil || e.URL == nil
}
