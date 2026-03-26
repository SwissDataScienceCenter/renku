# Handle long training runs

Long-running activities, such as model training runs, can benefit from a special workflow in your session. Sessions will stay running as long as there is user activity in the window or the CPU is used, but some cases the front-end application (e.g., JupyterLab or VSCode) may stop long-running processes.

In this situation, you should use `tmux`, a terminal multiplexer, to keep your session running.

## Implement command as a script

The first requirement is to implement your long running process as a script. The language does not matter, as long as you can run it from a command line.

For example, implement your training logic as a script `training.py`.

```python
#!/usr/bin/env python3
import time
from datetime import datetime, timedelta

SLEEP_INTERVAL = 10 * 60    # 10 minutes in seconds
MAX_DURATION = 2 * 60 * 60  # 2 hours in seconds

start_time = datetime.now()
end_time = start_time + timedelta(seconds=MAX_DURATION)
now = datetime.now()
filename = now.strftime("%Y_%m_%d-%H%M-run.txt")

while datetime.now() < end_time:
    now = datetime.now()
    with open(filename, "a") as f:
        f.write(now.strftime("%Y-%m-%d %H:%M:%S") + " ")
    print(f"Iteration done. Sleeping for {SLEEP_INTERVAL} seconds.")

    next_run = now + timedelta(seconds=SLEEP_INTERVAL)
    if next_run >= end_time:
        break
    counter = 0
    while datetime.now() < next_run:
        counter += 1
    with open(filename, "a") as f:
        f.write(f"counted to {counter} \n")
    # time.sleep(SLEEP_INTERVAL)

with open(filename, "a") as f:
    f.write("Done. Time limit reached.\n")
print("Done. Time limit reached.")
```

## Run using tmux

Open a terminal in your session and run the `tmux` command to start and enter a tmux session. Then run the script from within the session.

```
tmux
python training.py
```

N.b., if your session launcher environment was built from code on RenkuLab, `tmux` will be available. If the environment is built elsewhere, you may need to install `tmux` yourself.

## Reconnect

You can leave the session (as long as you keep it running) and close your browser window if you wish. You will still be able to return to the tmux session later by running the following in a terminal:

```
tmux attach
```

If you have multiple tmux sessions running, you can see a list by running `tmux ls` and attach to a specific session using `tmux attach -t [session name]`.

Consult the [Tmux Cheat Sheet](https://tmuxcheatsheet.com) for details on tmux and further tricks for managing sessions.
