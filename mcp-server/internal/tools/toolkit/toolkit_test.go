package toolkit

import (
	"errors"
	"fmt"
	"net/http"
	"strings"
	"testing"

	"github.com/SwissDataScienceCenter/renku/mcp-server/internal/renkuapi"
)

func TestClientErrorCarriesTheAPIMessage(t *testing.T) {
	err := APIError(http.StatusNotFound, []byte(`{"error": {"code": 1404, "message": "Project does not exist."}}`))
	if want := "status 404: Project does not exist."; !strings.Contains(err.Error(), want) {
		t.Errorf("error = %q, want it to contain %q", err, want)
	}
}

func TestFeatureAPIError(t *testing.T) {
	tests := []struct {
		name string
		err  func(int, []byte) error
		body string
		want string
	}{
		{"apps disabled", AppsAPIError, `{"error": {"code": 1404, "message": "Requested URL /api/data/apps not found"}}`, "Apps are not enabled on this Renku deployment"},
		{"missing app", AppsAPIError, `{"error": {"code": 1404, "message": "App with name 'a-1' does not exist or you do not have access to it."}}`, "status 404: App with name 'a-1' does not exist"},
		{"builds disabled", BuildsAPIError, `{"error": {"code": 1404, "message": "Requested URL /api/data/builds/01HXXXXXXXXXXXXXXXXXXXXXXX not found"}}`, "Image builds from code are not enabled on this Renku deployment"},
		{"missing build", BuildsAPIError, `{"error": {"code": 1404, "message": "Build does not exist."}}`, "status 404: Build does not exist."},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			err := tt.err(http.StatusNotFound, []byte(tt.body))
			if !strings.Contains(err.Error(), tt.want) {
				t.Errorf("error = %q, want it to contain %q", err, tt.want)
			}
		})
	}
}

func TestServerErrorIsGeneric(t *testing.T) {
	body := []byte(`{"error": {"code": 1500, "message": "database exploded at 10.0.0.12"}}`)
	err := APIError(http.StatusInternalServerError, body)
	if strings.Contains(err.Error(), "10.0.0.12") {
		t.Errorf("internal details leaked to the model: %q", err)
	}
	var toolErr *Error
	if !errors.As(err, &toolErr) || toolErr.Outcome != OutcomeUpstreamError || string(toolErr.Body) != string(body) {
		t.Errorf("the body must be kept for the logs, got %+v", toolErr)
	}
}

func TestRequestFailed(t *testing.T) {
	tooLarge := RequestFailed(fmt.Errorf("reading: %w", renkuapi.ErrResponseTooLarge))
	if !strings.Contains(tooLarge.Error(), "too large") {
		t.Errorf("error = %q", tooLarge)
	}
	if unreachable := RequestFailed(errors.New("connection refused")); !strings.Contains(unreachable.Error(), "could not be reached") {
		t.Errorf("error = %q", unreachable)
	}
}

func TestPagination(t *testing.T) {
	tests := []struct {
		name    string
		headers map[string]string
		want    string
	}{
		{"more pages", map[string]string{"page": "1", "per-page": "20", "total": "45", "total-pages": "3"}, "Page 1 of 3 (45 total, 20 per page). Call again with page=2 for more."},
		{"search headers, last page", map[string]string{"x-page": "2", "x-per-page": "10", "x-total": "15", "x-total-pages": "2"}, "Page 2 of 2 (15 total, 10 per page)."},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			h := http.Header{}
			for k, v := range tt.headers {
				h.Set(k, v)
			}
			p := PaginationFrom(h)
			if p == nil {
				t.Fatal("no pagination")
			}
			if got := p.Summary(); got != tt.want {
				t.Errorf("summary = %q, want %q", got, tt.want)
			}
		})
	}
	if p := PaginationFrom(http.Header{}); p != nil {
		t.Errorf("pagination without headers = %+v", p)
	}
}
