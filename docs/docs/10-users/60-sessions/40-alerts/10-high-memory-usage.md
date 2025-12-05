---
title: High Memory Usage
---

## Why This Alert Was Triggered

Your session is using more than 90% of its allocated memory capacity. This can happen when:

- Your code is processing large datasets
- Your code has memory leaks or is accumulating data in memory without releasing it
- The memory allocation for your session is too small for your workload

## What This Means

When memory usage is this high, your session is at risk of:

- Becoming unresponsive or slow
- Being terminated by the system if it exceeds its memory limit ([Out of Memory kill](./20-session-restarted.md))
- Losing unsaved work if the session terminates unexpectedly

## Steps to Remedy

### Immediate Actions

1. Commit and push any important changes to avoid losing them
2. Free up memory:
   - Stop any running processes you don't need
   - Clear large variables from memory in your notebooks or scripts (e.g., `del variable` in Python)
   - Restart your Python kernel if using Jupyter notebooks

### Longer-Term Solutions

1. Optimise your code:
   - Process data in smaller chunks
   - Use generators or iterators instead of loading full datasets
   - Delete variables you no longer need during execution
   - Use memory-efficient data types and libraries

2. Request more memory:
   - Stop your current session
   - When starting a new session, select a resource class with more memory, if available
   - See [Resource Pools and Classes](../30-resource-pools-and-classes.md) for more information

3. Profile memory usage:
   - Use memory profiling tools to identify which parts of your code use the most memory
   - For Python: `memory_profiler`, `tracemalloc`, or `guppy3`
   - For R: `pryr::mem_used()` or `profmem`

## Prevention

- Choose an appropriate resource class when starting sessions based on your expected workload
- Test code with small datasets first to estimate memory requirements

