// Package startsession is the start_session tool.
package startsession

import (
	"context"
	"net/http"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/notebooks"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "start_session",
	Description: "Start a session from a session launcher, or return the existing one if a session for " +
		"that launcher is already running. Usually only `launcher_id` is needed; get it from " +
		"list_project_session_launchers. The response contains the session name and its URL, " +
		"which the user can open in a browser once the status is `running`. Starting can take " +
		"a few minutes; poll with get_session.\n\n" +
		"This also starts a job from a launcher whose `launcher_type` is `non-interactive`. A job " +
		"needs a `submission_id`, and can override the command and args of the container.\n\n" +
		"Starting is idempotent: `created` is false if a matching session already existed and was " +
		"returned instead of a new one. Then either use that session, or stop it and retry, for " +
		"a job with a different `submission_id`; do not assume a new job started.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(false)},
}

type input struct {
	LauncherID         string   `json:"launcher_id" jsonschema:"The session launcher ID, a ULID."`
	ResourceClassID    *int     `json:"resource_class_id,omitempty" jsonschema:"Use this resource class instead of the launcher's default."`
	DiskStorage        *int     `json:"disk_storage,omitempty" jsonschema:"The disk storage of the session in GB, instead of the launcher's default."`
	SubmissionID       *string  `json:"submission_id,omitempty" jsonschema:"Required for jobs: a unique ID for this submission, matching ^[a-z][-0-9a-z]{3,19}$. Reuse it to retry the same submission."`
	JobCommandOverride []string `json:"job_command_override,omitempty" jsonschema:"Jobs only: replaces the command of the container."`
	JobArgsOverride    []string `json:"job_args_override,omitempty" jsonschema:"Jobs only: replaces the args of the container."`
}

type output struct {
	Created bool                       `json:"created" jsonschema:"Whether a new session was started, false if an existing one was returned."`
	Session *notebooks.SessionResponse `json:"session"`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		body := notebooks.SessionPostRequest{
			LauncherId:      in.LauncherID,
			ResourceClassId: in.ResourceClassID,
			DiskStorage:     in.DiskStorage,
			SubmissionId:    in.SubmissionID,
		}
		if in.JobCommandOverride != nil {
			body.JobCommandOverride = &in.JobCommandOverride
		}
		if in.JobArgsOverride != nil {
			body.JobArgsOverride = &in.JobArgsOverride
		}
		res, err := api.Notebooks.PostSessionsWithResponse(ctx, body, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		switch {
		case res.JSON201 != nil:
			return nil, &output{Created: true, Session: res.JSON201}, nil
		case res.JSON200 != nil && res.StatusCode() == http.StatusOK:
			return nil, &output{Created: false, Session: res.JSON200}, nil
		}
		return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
	}
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
