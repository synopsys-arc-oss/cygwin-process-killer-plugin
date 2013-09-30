###
#!/bin/bash
# Script kill Cygwin process tree.
# In order to work properly, script should be launched by Cygwin DLL, which
# executes target script.
#
# License: cc-wiki with attribution required
# Code source: http://stackoverflow.com/questions/523878/how-to-terminate-scripts-process-tree-in-cygwin-bash-from-bash-script
# Authors: Adam Rosenfield, Barry Kelly
###
function usage
{
    echo "usage: $(basename $0) [-c] [-<sigspec>] <pid>..."
    echo "Recursively kill the process tree(s) rooted by <pid>."
    echo "Options:"
    echo "  -c        Only kill children; don't kill root"
    echo "  <sigspec> Arbitrary argument to pass to kill, expected to be signal specification"
    exit 1
}

kill_parent=1
sig_spec=-9

function do_kill # <pid>...
{
    kill "$sig_spec" "$@"
}

function kill_children # pid
{
    local target=$1
    local pid=
    local ppid=
    local i
    # Returns alternating ids: first is pid, second is parent
    for i in $(ps -f | tail --lines=+2 | cut -b 10-24); do
        if [ ! -n "$pid" ]; then
            # first in pair
            pid=$i
        else
            # second in pair
            ppid=$i
            (( ppid == target && pid != $$ )) && {
                kill_children $pid
                do_kill $pid
            }
            # reset pid for next pair
            pid=
        fi
    done

}

test -n "$1" || usage

while [ -n "$1" ]; do
    case "$1" in
        -c)
            kill_parent=0
            ;;

        -*)
            sig_spec="$1"
            ;;

        *)
            kill_children $1
            (( kill_parent )) && do_kill $1
            ;;
    esac
    shift
done