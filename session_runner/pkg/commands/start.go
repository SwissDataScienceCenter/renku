package commands

import (
	"os"
	"os/signal"
	"syscall"

	"github.com/SwissDataScienceCenter/renku/session_runner/pkg/runner"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

const (
	urlFlag   = "url"
	tokenFlag = "token"
)

func StartCmd() *cobra.Command {
	cmd := &cobra.Command{
		Use:     "start",
		Short:   "Start the session runner",
		PreRunE: initialize,
		RunE:    run,
	}

	cmd.Flags().String(urlFlag, "", "URL of the RenkuLab instance")
	cmd.Flags().String(tokenFlag, "", "registration token")

	return cmd
}

func initialize(cmd *cobra.Command, args []string) error {
	return viper.BindPFlags(cmd.Flags())
}

func run(cmd *cobra.Command, args []string) error {
	url := viper.GetString(urlFlag)
	token := viper.GetString(tokenFlag)

	r, err := runner.NewRunner(runner.WithRenkuURL(url), runner.WithRegistrationToken(token))
	if err != nil {
		return err
	}

	ctx, cancel := signal.NotifyContext(cmd.Context(), os.Interrupt, syscall.SIGTERM)
	defer cancel()
	if err := r.Start(ctx); err != nil {
		return err
	}

	return nil
}
