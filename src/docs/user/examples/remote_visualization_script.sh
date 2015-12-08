#!/bin/bash
# Create a X server with Xvnc and run a simple graphical app

Xvnc :10 -geometry 1280x1024 &
xvnc_pid=$!
ps -p $xvnc_pid
if [ $? -eq 0 ]; then
    # magic string to enable remote visualization
    echo "PA_REMOTE_CONNECTION;$variables_PA_JOB_ID;$variables_PA_TASK_ID;vnc;$(hostname):5910"
    export DISPLAY=:10
    # RUN YOUR GUI BASED APPLICATION HERE
    xclock

    kill $xvnc_pid
        echo "[debug] Display closed"
        exit
fi