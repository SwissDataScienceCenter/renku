// Package updatesessionlauncher is the update_session_launcher tool.
package updatesessionlauncher

import (
	"context"
	"errors"
	"reflect"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/session"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "update_session_launcher",
	Description: "Update a session launcher. Fields that are left out stay unchanged. Get the current " +
		"launcher with get_session_launcher first. To switch to another global environment, set " +
		"only `environment.id`; otherwise the fields of `environment` change the launcher's custom " +
		"environment.\n\n" +
		"If a web app in a session cannot be configured to serve under $RENKU_BASE_URL_PATH, set " +
		"`environment.strip_path_prefix` to true so Renku removes the prefix before forwarding " +
		"requests. For a launcher built from code, changing the build fields starts a new build: " +
		"find it with list_environment_builds and wait for it with wait_for_build.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(false), IdempotentHint: true},
}

type environment struct {
	ID *string `json:"id,omitempty" jsonschema:"Switch to this global environment, from list_environments. Leave out all other fields when set."`

	ContainerImage   *string `json:"container_image,omitempty" jsonschema:"image only: the container image."`
	DefaultURL       *string `json:"default_url,omitempty" jsonschema:"image only: the path to open in the session."`
	Port             *int    `json:"port,omitempty" jsonschema:"image only: the port the image serves on."`
	UID              *int    `json:"uid,omitempty" jsonschema:"image only: the user ID to run as."`
	GID              *int    `json:"gid,omitempty" jsonschema:"image only: the group ID to run as."`
	WorkingDirectory *string `json:"working_directory,omitempty" jsonschema:"image only: where the session starts."`
	MountDirectory   *string `json:"mount_directory,omitempty" jsonschema:"image only: where the persistent storage is mounted."`
	StripPathPrefix  *bool   `json:"strip_path_prefix,omitempty" jsonschema:"Remove the session's base path from requests before they reach the app."`

	Command []string `json:"command,omitempty" jsonschema:"Replaces the entrypoint of the image."`
	Args    []string `json:"args,omitempty" jsonschema:"Replaces the command (CMD) of the image."`

	Repository         *string `json:"repository,omitempty" jsonschema:"build only: the git repository URL to build from."`
	BuilderVariant     *string `json:"builder_variant,omitempty" jsonschema:"build only: python or r."`
	FrontendVariant    *string `json:"frontend_variant,omitempty" jsonschema:"build only: jupyterlab, vscodium, rstudio or ttyd for sessions, none for apps."`
	RepositoryRevision *string `json:"repository_revision,omitempty" jsonschema:"build only: the git branch, tag or commit to build."`
	ContextDir         *string `json:"context_dir,omitempty" jsonschema:"build only: the folder of the repository to build from."`
}

type input struct {
	LauncherID      string           `json:"launcher_id" jsonschema:"The session launcher ID, a ULID."`
	Name            *string          `json:"name,omitempty"`
	Description     *string          `json:"description,omitempty"`
	ResourceClassID *int             `json:"resource_class_id,omitempty" jsonschema:"The resource class, from list_resource_pools."`
	DiskStorage     *int             `json:"disk_storage,omitempty" jsonschema:"The disk storage of the sessions in GB."`
	EnvVariables    []session.EnvVar `json:"env_variables,omitempty" jsonschema:"Replaces all environment variables of the sessions."`
	Environment     *environment     `json:"environment,omitempty"`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		body := session.SessionLauncherPatch{
			Name:            in.Name,
			Description:     in.Description,
			ResourceClassId: in.ResourceClassID,
			DiskStorage:     in.DiskStorage,
		}
		if in.EnvVariables != nil {
			body.EnvVariables = &in.EnvVariables
		}
		if in.Environment != nil {
			body.Environment = &session.SessionLauncherPatch_Environment{}
			if err := setEnvironment(body.Environment, *in.Environment); err != nil {
				return nil, nil, err
			}
		}
		res, err := api.Session.PatchSessionLaunchersLauncherIdWithResponse(ctx, in.LauncherID, body, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON200 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return nil, res.JSON200, nil
	}
}

// setEnvironment sets the variant of the environment union that env describes.
func setEnvironment(dst *session.SessionLauncherPatch_Environment, env environment) error {
	if env.ID != nil {
		if !reflect.DeepEqual(env, environment{ID: env.ID}) {
			return errors.New("environment.id switches to a global environment and cannot be combined with other environment fields")
		}
		return dst.FromEnvironmentIdOnlyPatch(session.EnvironmentIdOnlyPatch{Id: env.ID})
	}
	patch := session.EnvironmentPatchInLauncher{
		ContainerImage:   env.ContainerImage,
		DefaultUrl:       env.DefaultURL,
		Port:             env.Port,
		Uid:              env.UID,
		Gid:              env.GID,
		WorkingDirectory: env.WorkingDirectory,
		MountDirectory:   env.MountDirectory,
		StripPathPrefix:  env.StripPathPrefix,
	}
	if env.Command != nil {
		patch.Command = &env.Command
	}
	if env.Args != nil {
		patch.Args = &env.Args
	}
	build := session.BuildParametersPatch{
		Repository:         env.Repository,
		BuilderVariant:     env.BuilderVariant,
		FrontendVariant:    env.FrontendVariant,
		RepositoryRevision: env.RepositoryRevision,
		ContextDir:         env.ContextDir,
	}
	if build != (session.BuildParametersPatch{}) {
		patch.BuildParameters = &build
	}
	return dst.FromEnvironmentPatchInLauncher(patch)
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
