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
   # Find the largest files in your Renku volume
   du -h $RENKU_MOUNT_DIR | sort -h | tail -20
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
   - Save any unsaved work, push all data you wish to keep to your code repositories and/or data connectors and shut down your current session
   - Increase the disk storage by editing the session launcher. This will only be possible if the maximum disk storage for the selected resource class size has not already been met. If it has, you may be able to switch to a different resource class with a larger maximum disk size.
   - Restart your session.
   - See [Resource Pools and Classes](../30-resource-pools-and-classes.md) for more information

3. Optimise your workflow:
   - Configure applications to use temporary directories that you can clean regularly
   - Write outputs to compressed formats when possible
   - Implement automatic cleanup of intermediate files in your scripts

## Prevention

- Monitor disk usage regularly: `df -h $RENKU_MOUNT_DIR`
- Set up your code to clean up temporary files automatically
- Choose an appropriate resource class with sufficient storage when starting sessions
- Keep only necessary files in your session workspace
- Use a [Data Connector](../../40-data/00-data.md) to store and access large datasets from external storage instead of keeping them on your session disk

