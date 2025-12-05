---
title: Session Restarted (Out of Memory)
---

## Why This Alert Was Triggered

Your session ran out of memory and was automatically killed and restarted. This happens when:

- Your session tried to use more memory than was allocated to it.
- Memory usage spiked suddenly, exceeding the limit before you could react
- A process had a memory leak that gradually consumed all available memory
- Multiple memory-intensive operations ran simultaneously

## What This Means

When a session is restarted:

- Any changes not committed to Git and pushed to remote, or saved to disk are gone
- In-progress processes were terminated
- Your Python kernel, R session, or other runtime environments have been reset

## Steps to Remedy

### Immediate Actions

1. Check what was lost:
   - Review your Git status to see if you have uncommitted changes: `git status`
   - Look for any auto-saved files or checkpoints
   - Check if your notebook or IDE has auto-recovery features

2. Prevent recurrence before restarting work:
   - Review what you were running when the restart occurred
   - Identify memory-intensive operations that need optimisation

### Longer-Term Solutions

1. Optimise memory usage:
   - Process data in smaller chunks instead of loading everything at once
   - Use memory-efficient data structures and algorithms
   - Stream data from disk rather than loading it all into memory
   - Delete large variables when you're done with them: `del variable` in Python, `rm(variable)` in R
   - Use generators or iterators for large datasets

2. Request more memory:
   - If your workload needs more memory, stop your session and restart with a larger resource class, if available
   - See [Resource Pools and Classes](../30-resource-pools-and-classes.md) for information

3. Monitor memory usage:
   - Add memory profiling to your code
   - Use the [High Memory Usage](./10-high-memory-usage.md) alert as an early warning system

4. Save work frequently:
   - Commit and push changes to Git regularly
   - Save intermediate results to disk
   - Use checkpoint systems in long-running computations

## Prevention

- Choose an appropriate resource class when starting sessions based on your expected workload
- Test code with small datasets first to estimate memory requirements
