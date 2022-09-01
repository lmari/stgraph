#!/bin/bash

# from https://github.com/kaueraal/run_scaled

# Default settings
SCALING_FACTOR=2
USE_OPENGL="auto"
SLEEPTIME=1

print_help() {
                echo "usage: run_scale [--scale=scaling_factor] [--opengl=auto|yes|no] [--sleep=sleeptime] application"
                echo ""
                echo "--scale  is set to 2 by default. Fractional scales are supported."
                echo "--opengl is set to auto by default. If you get rendering errors, especially"
                echo "         when the window is resized, try setting it to no."
                echo "--sleep  sets how many seconds to wait after starting xpra to run the given"
                echo "         application. It is set to 1 by default."
}

if [ $# = 0 ]
    then print_help
        exit 1
fi

while true
    do if [ $# -gt 0 ]
        then case $1 in
            --scale=*)
                SCALING_FACTOR="${1#*=}"
                shift
            ;;
            --opengl=*)
                USE_OPENGL="${1#*=}"
                shift
            ;;
            --sleep=*)
                SLEEPTIME="${1#*=}"
                shift
            ;;
            --)
                shift
                break
            ;;
            --*)
                print_help
                exit 1
            ;;
            *)
                break
            ;;
        esac
    else
        echo "No application given!"
        exit 2
    fi
done

declare -i UNSCALED_RESOLUTION_X
declare -i UNSCALED_RESOLUTION_Y
UNSCALED_RESOLUTION_X=`xrandr | sed -n -e 's/Screen 0:.*current \([0-9]\+\) x \([0-9]\+\).*/\1/p'`
UNSCALED_RESOLUTION_Y=`xrandr | sed -n -e 's/Screen 0:.*current \([0-9]\+\) x \([0-9]\+\).*/\2/p'`

UNSCALED_RESOLUTION="$( bc <<<"$UNSCALED_RESOLUTION_X / $SCALING_FACTOR" )x$( bc <<<"$UNSCALED_RESOLUTION_Y / $SCALING_FACTOR" )"

DISPLAYNUM=:`shuf -i 10000-99999999 -n 1`
ESCAPED_PARAMS=`printf '%q ' "$@"`
xpra start "$DISPLAYNUM" --xvfb="Xvfb +extension Composite -screen 0 ${UNSCALED_RESOLUTION}x24+32 -nolisten tcp -noreset  -auth \$XAUTHORITY" --env=GDK_SCALE=1 --env=GDK_DPI_SCALE=1 --start-child="$ESCAPED_PARAMS" --exit-with-children
sleep "$SLEEPTIME"
xpra attach "$DISPLAYNUM" "--desktop-scaling=$SCALING_FACTOR" "--opengl=$USE_OPENGL" || xpra stop "$DISPLAYNUM"
