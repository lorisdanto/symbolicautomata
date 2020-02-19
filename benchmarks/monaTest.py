# how to run this program:
# this program is tested on python 2.7.10
# first we need EasyProcess module
# credit to http://easyprocess.readthedocs.io/en/latest/
# run $pip install EasyProcess
# to install this module.
# after installation, run this program as normal python files
# the default output will be printed to the console
# thus, use '>>' to redirect output to file instead
# e.g $python monaTest.py >> output.txt
# the output will be directed to the output.txt
# make sure to delete old output.txt before re-run the program,
# the old information in output.txt will not be automatically covered by the new input

import os
from easyprocess import EasyProcess
str = 'Total time: '

def main():
    dir = os.getcwd() # this returns current directory of monaTest.py
    dir = os.path.join(dir, 'MonaFiles') # this joins the current directory and the directory to MonaFiles
    #print header of the output in format
    print '%-25s %-25s %-25s'%('File Name', 'Total Time', 'Satisfiability')
    for root, dirs, files in os.walk(dir):
        for fname in files:
            #print fname
            fullName = 'MonaFiles/'+fname
            if fname.endswith('.mona'):
                callProcess('mona '+ fullName, fname)
            


def callProcess(command, filename):
    #set timeout in seconds below in .call(timeout=XX)
    proc = EasyProcess(command).call(timeout=5)
    totalTime =''
    output = proc.stdout
    if output is None:
        print '%-25s %-25s %-25s'%(filename, 'NoneType', '')
    else:
        # below will check the information of each file
        if '100%' in output:
            # if the AUTOMATON CONSTRUCTION is 100%,
            # that means this MONA file has complete result
            for line in output.split(os.linesep):
                if str in line:
                    # extract total time
                    totalTime= line[line.index(str)+len(str):].encode('raw_unicode_escape').decode('utf8')
            if 'unsatisfiable' in output:
                satisfiability = 'false'
            else: satisfiability = 'true'
        else:
        # if the AUTOMATON CONSTRUCTION is not 100%,
        # that means it exceeds the timeout limit
            totalTime = 'timeout'
            satisfiability= ''
                #print each file's info in format
        if totalTime =='':
            totalTime = 'Ref before Assign'
        print '%-25s %-25s %-25s'%(filename, totalTime, satisfiability)


main()