package main

import (
	"log/slog"
	"os"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/commands"
	"github.com/spf13/cobra"
)

func main() {
	slog.SetDefault(slog.New(slog.NewJSONHandler(os.Stdout, nil)))

	cmd := commands.RootCmd()
	cobra.CheckErr(cmd.Execute())
}
