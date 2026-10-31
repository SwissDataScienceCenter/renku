package updatesessionlauncher

import (
	"encoding/json"
	"testing"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi/session"
)

func TestSetEnvironment(t *testing.T) {
	id, revision := "01J0000000000000000000000C", "v2"
	tests := []struct {
		name string
		env  environment
		want string
	}{
		{"global", environment{ID: &id}, `{"id":"01J0000000000000000000000C"}`},
		{"build parameters are nested", environment{RepositoryRevision: &revision, Args: []string{}}, `{"args":[],"build_parameters":{"repository_revision":"v2"}}`},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			var got session.SessionLauncherPatch_Environment
			if err := setEnvironment(&got, tt.env); err != nil {
				t.Fatal(err)
			}
			raw, _ := json.Marshal(got)
			if string(raw) != tt.want {
				t.Errorf("environment = %s, want %s", raw, tt.want)
			}
		})
	}
}

func TestSetEnvironmentRejectsAnIDWithOtherFields(t *testing.T) {
	id, image := "01J0000000000000000000000C", "org/app:1"
	var got session.SessionLauncherPatch_Environment
	if err := setEnvironment(&got, environment{ID: &id, ContainerImage: &image}); err == nil {
		t.Error("expected an error")
	}
}
