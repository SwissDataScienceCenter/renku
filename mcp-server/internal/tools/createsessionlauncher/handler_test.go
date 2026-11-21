package createsessionlauncher

import (
	"encoding/json"
	"testing"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/session"
)

func TestSetEnvironment(t *testing.T) {
	port := 8501
	tests := []struct {
		name string
		env  environment
		want string
	}{
		{"global", environment{ID: "01J0000000000000000000000C"}, `{"id":"01J0000000000000000000000C"}`},
		{
			"image named after the launcher",
			environment{ImageSource: "image", ContainerImage: "org/app:1", Port: &port, Command: []string{"streamlit"}},
			`{"command":["streamlit"],"container_image":"org/app:1","environment_image_source":"image","environment_kind":"CUSTOM","name":"my launcher","port":8501}`,
		},
		{
			"build",
			environment{ImageSource: "build", Repository: "https://github.com/org/repo", BuilderVariant: "python", FrontendVariant: "none"},
			`{"builder_variant":"python","environment_image_source":"build","frontend_variant":"none","repository":"https://github.com/org/repo"}`,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			var got session.SessionLauncherPost_Environment
			if err := setEnvironment(&got, tt.env, "my launcher"); err != nil {
				t.Fatal(err)
			}
			raw, _ := json.Marshal(got)
			if string(raw) != tt.want {
				t.Errorf("environment = %s, want %s", raw, tt.want)
			}
		})
	}
}

func TestSetEnvironmentRejectsAnEnvironmentWithoutASource(t *testing.T) {
	var got session.SessionLauncherPost_Environment
	if err := setEnvironment(&got, environment{ContainerImage: "org/app:1"}, "my launcher"); err == nil {
		t.Error("expected an error")
	}
}
