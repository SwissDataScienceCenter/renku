---
title: Low Disk Space
---

## Why This Alert Was Triggered

Your session has less than 10% of its disk space remaining. This typically happens when:

- You've downloaded or generated large files in your session
- Intermediate files have accumulated over time
- Log files or cache directories have grown large

## What This Means

When disk space is this low:

- You may not be able to save new files
- Running processes might fail
- Your session may become unstable or crash

## Steps to Remedy

### Immediate Actions

1. Push any important changes to your Git repository or external storage to avoid data loss.

2. Identify large files:
   ```bash
   # Find the largest files in your home directory
   du -h ~ | sort -h | tail -20
   ```

3. Free up space:
   - Delete temporary files, old logs, or intermediate computation results
   - Remove downloaded datasets you no longer need

### Longer-Term Solutions

1. Manage data more efficiently:
   - Use a [Data Connector](../../40-data/00-data.md) to access data from external storage without copying it to your session's disk (e.g., S3, Azure Blob Storage, external data repositories)
   - Use data streaming instead of downloading entire datasets
   - Clean up output files regularly

2. Request more disk space:
   - Stop your current session
   - When starting a new session, if available, select a resource class with more storage. You will need to manually transfer any necessary files from your old session to the new one.
   - See [Resource Pools and Classes](../30-resource-pools-and-classes.md) for more information

3. Optimise your workflow:
   - Configure applications to use temporary directories that you can clean regularly
   - Write outputs to compressed formats when possible
   - Implement automatic cleanup of intermediate files in your scripts

## Prevention

- Monitor disk usage regularly: `df -h ~`
- Set up your code to clean up temporary files automatically
- Choose an appropriate resource class with sufficient storage when starting sessions
- Keep only necessary files in your session workspace
- Use a [Data Connector](../../40-data/00-data.md) to store and access large datasets from external storage instead of keeping them on your session disk

