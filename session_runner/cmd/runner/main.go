package main

import (
	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/commands"
	"github.com/spf13/cobra"
)

func main() {
	cmd := commands.RootCmd()
	cobra.CheckErr(cmd.Execute())
}
