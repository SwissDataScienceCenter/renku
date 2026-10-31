// Package createsessionlauncher is the create_session_launcher tool.
package createsessionlauncher

import (
	"context"
	"errors"

	"github.com/modelcontextprotocol/go-sdk/mcp"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/session"
	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/tools/toolkit"
)

var tool = mcp.Tool{
	Name: "create_session_launcher",
	Description: "Create a session launcher in a project. A launcher defines the environment (image) " +
		"and resources of the sessions, jobs or app started from it. Call list_resource_pools first " +
		"and pass the smallest matching resource class as `resource_class_id`. Returns the launcher " +
		"ID, needed by start_session and launch_app, and its environment ID.\n\n" +
		"`launcher_type`: leave it out for interactive sessions, `non-interactive` for jobs started " +
		"with start_session, `app` for an app started with launch_app. An `app` launcher is only " +
		"accepted in a public project; ask the user before making a project public.\n\n" +
		"Choosing the environment. If the project has a repository (see get_project), look in it for " +
		"requirements.txt, pyproject.toml with uv.lock or poetry.lock, environment.yml, renv.lock or " +
		"*.R files. If any is present, offer the user to build the image from the repository, which " +
		"installs the project's exact dependencies. Three ways to set `environment`:\n" +
		"1. Global environment, for users with no image and no repository: only `id`, from " +
		"list_environments. Show the user the list and let them pick.\n" +
		"2. Build from code: `environment_image_source` `build` with `repository`, `builder_variant` " +
		"and `frontend_variant`. After creating the launcher, find the build with " +
		"list_environment_builds and poll get_build until it succeeded before starting a session.\n" +
		"3. Custom image: `environment_image_source` `image` with `container_image`, and the `port`, " +
		"`uid`, `gid` and directories the image needs.\n\n" +
		"Apps (`launcher_type` `app`), with option 2 or 3:\n" +
		"- The served process must bind $RENKU_SESSION_PORT on 0.0.0.0. This is the most common " +
		"reason an app fails: it stays pending, then fails with nothing obvious in the logs.\n" +
		"- With option 2, use `frontend_variant` `none` and give the repository a Procfile with a " +
		"`web:` process that serves the app.\n" +
		"- With option 3, the image must run as non-root, as apps drop all capabilities.\n" +
		"- Apps are served at the root of their own hostname: there is no base path, so skip the " +
		"session guidance below.\n\n" +
		"Sessions and jobs: these environment variables are set in the container: " +
		"RENKU_BASE_URL_PATH (the URL path prefix of the session), RENKU_BASE_URL (the full URL with " +
		"that prefix), RENKU_SESSION_PORT (the port to listen on), RENKU_SESSION_IP (the address to " +
		"bind, 0.0.0.0) and RENKU_SESSION (1 in Renku). A web app in a session must serve under " +
		"RENKU_BASE_URL_PATH, e.g.:\n" +
		"- Streamlit: streamlit run app.py --server.baseUrlPath $RENKU_BASE_URL_PATH\n" +
		"- Dash: Dash(__name__, requests_pathname_prefix=os.environ['RENKU_BASE_URL_PATH'] + '/')\n" +
		"- FastAPI: FastAPI(root_path=os.environ['RENKU_BASE_URL_PATH'])\n" +
		"- Panel: pn.serve(..., prefix=os.environ['RENKU_BASE_URL_PATH'])\n" +
		"If the app cannot be configured that way, set `strip_path_prefix` on the environment so " +
		"Renku removes the prefix before forwarding requests; the app then cannot build correct " +
		"absolute URLs.",
	Annotations: &mcp.ToolAnnotations{DestructiveHint: new(false)},
}

type environment struct {
	ID          string `json:"id,omitempty" jsonschema:"A global environment ID, from list_environments. Leave out all other fields when set."`
	ImageSource string `json:"environment_image_source,omitempty" jsonschema:"For a custom environment: image to run a container image, build to build one from a git repository."`

	ContainerImage   string  `json:"container_image,omitempty" jsonschema:"image only: the container image, e.g. renku/renkulab-py:3.10-0.18.1."`
	Name             string  `json:"name,omitempty" jsonschema:"image only: the name of the environment, the launcher name if left out."`
	DefaultURL       *string `json:"default_url,omitempty" jsonschema:"image only: the path to open in the session, /lab by default."`
	Port             *int    `json:"port,omitempty" jsonschema:"image only: the port the image serves on, 8080 by default."`
	UID              *int    `json:"uid,omitempty" jsonschema:"image only: the user ID to run as, 1000 by default."`
	GID              *int    `json:"gid,omitempty" jsonschema:"image only: the group ID to run as, 1000 by default."`
	WorkingDirectory *string `json:"working_directory,omitempty" jsonschema:"image only: where the session starts, the image's working directory by default."`
	MountDirectory   *string `json:"mount_directory,omitempty" jsonschema:"image only: where the persistent storage is mounted, the working directory by default."`
	StripPathPrefix  *bool   `json:"strip_path_prefix,omitempty" jsonschema:"image only: remove the session's base path from requests before they reach the app."`

	Command []string `json:"command,omitempty" jsonschema:"Replaces the entrypoint of the image."`
	Args    []string `json:"args,omitempty" jsonschema:"Replaces the command (CMD) of the image."`

	Repository         string  `json:"repository,omitempty" jsonschema:"build only: the git repository URL to build from."`
	BuilderVariant     string  `json:"builder_variant,omitempty" jsonschema:"build only: python or r."`
	FrontendVariant    string  `json:"frontend_variant,omitempty" jsonschema:"build only: jupyterlab, vscodium, rstudio or ttyd for sessions, none for apps."`
	RepositoryRevision *string `json:"repository_revision,omitempty" jsonschema:"build only: the git branch, tag or commit to build, the default branch if left out."`
	ContextDir         *string `json:"context_dir,omitempty" jsonschema:"build only: the folder of the repository to build from, the root if left out."`
}

type input struct {
	ProjectID       string           `json:"project_id" jsonschema:"The project ID, a ULID."`
	Name            string           `json:"name" jsonschema:"The launcher name."`
	LauncherType    string           `json:"launcher_type,omitempty" jsonschema:"interactive (the default), non-interactive for jobs or app."`
	Description     *string          `json:"description,omitempty"`
	ResourceClassID *int             `json:"resource_class_id,omitempty" jsonschema:"The resource class, from list_resource_pools. The default resource class if left out."`
	DiskStorage     *int             `json:"disk_storage,omitempty" jsonschema:"The disk storage of the sessions in GB."`
	EnvVariables    []session.EnvVar `json:"env_variables,omitempty" jsonschema:"Environment variables of the sessions."`
	Environment     environment      `json:"environment"`
}

func handler(api *renkuapi.Clients) mcp.ToolHandlerFor[input, any] {
	return func(ctx context.Context, req *mcp.CallToolRequest, in input) (*mcp.CallToolResult, any, error) {
		body := session.SessionLauncherPost{
			ProjectId:       in.ProjectID,
			Name:            in.Name,
			LauncherType:    session.Interactive,
			Description:     in.Description,
			ResourceClassId: in.ResourceClassID,
			DiskStorage:     in.DiskStorage,
		}
		if in.LauncherType != "" {
			body.LauncherType = session.LauncherType(in.LauncherType)
		}
		if in.EnvVariables != nil {
			body.EnvVariables = &in.EnvVariables
		}
		if err := setEnvironment(&body.Environment, in.Environment, in.Name); err != nil {
			return nil, nil, err
		}
		res, err := api.Session.PostSessionLaunchersWithResponse(ctx, body, toolkit.CallerHeaders(ctx, req))
		if err != nil {
			return nil, nil, toolkit.RequestFailed(err)
		}
		if res.JSON201 == nil {
			return nil, nil, toolkit.APIError(res.StatusCode(), res.Body)
		}
		return nil, res.JSON201, nil
	}
}

// setEnvironment sets the variant of the environment union that env describes. Image environments
// are named after the launcher unless env has a name.
func setEnvironment(dst *session.SessionLauncherPost_Environment, env environment, launcherName string) error {
	if env.ID != "" {
		return dst.FromEnvironmentIdOnlyPost(session.EnvironmentIdOnlyPost{Id: env.ID})
	}
	var custom session.EnvironmentPostInLauncher
	var err error
	switch env.ImageSource {
	case string(session.Image):
		name := env.Name
		if name == "" {
			name = launcherName
		}
		err = custom.FromEnvironmentPostInLauncherHelper(session.EnvironmentPostInLauncherHelper{
			EnvironmentImageSource: session.Image,
			EnvironmentKind:        session.CUSTOM,
			Name:                   name,
			ContainerImage:         env.ContainerImage,
			DefaultUrl:             env.DefaultURL,
			Port:                   env.Port,
			Uid:                    env.UID,
			Gid:                    env.GID,
			WorkingDirectory:       env.WorkingDirectory,
			MountDirectory:         env.MountDirectory,
			StripPathPrefix:        env.StripPathPrefix,
			Command:                nilIfEmpty(env.Command),
			Args:                   nilIfEmpty(env.Args),
		})
	case string(session.EnvironmentImageSourceBuildBuild):
		err = custom.FromBuildParametersPost(session.BuildParametersPost{
			EnvironmentImageSource: session.EnvironmentImageSourceBuildBuild,
			Repository:             env.Repository,
			BuilderVariant:         env.BuilderVariant,
			FrontendVariant:        env.FrontendVariant,
			RepositoryRevision:     env.RepositoryRevision,
			ContextDir:             env.ContextDir,
			Command:                nilIfEmpty(env.Command),
			Args:                   nilIfEmpty(env.Args),
		})
	default:
		return errors.New("environment needs either an id, or environment_image_source image or build")
	}
	if err != nil {
		return err
	}
	return dst.FromEnvironmentPostInLauncher(custom)
}

func nilIfEmpty(s []string) *[]string {
	if len(s) == 0 {
		return nil
	}
	return &s
}

// Register adds the tool to the mcp server.
func Register(s *mcp.Server, api *renkuapi.Clients) {
	mcp.AddTool(s, &tool, handler(api))
}
