package reconciler

import (
	"context"
	"log"
)

type RunnerReconciler struct{}

type SessionRef struct {
	ID string
}

func (r *RunnerReconciler) Reconcile(ctx context.Context, session SessionRef) error {
	log.Printf("TODO: handle Reconcile(): %+v", session)
	return nil
}
