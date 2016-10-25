#pip install openpyxl
#put this file and the the results.xlsx in the same folder
#output will be in the file text.xlsx
#for some reason this openpyxl module has some internal error but this does not affect the data.
#Thus I copy the column in the test.xlsx and copy that column to results.xlsx in excel.
from openpyxl import load_workbook



def main():
    numOfRow=2;
    xfile = load_workbook('results.xlsx')
    #sheet = xfile.get_sheet_by_name('Emptiness-complete')
    sheet = xfile.active
    
    with open('toExcel.txt') as f:
        for line in f:
            #get time from file
            line = line.split()
            time= int(line[1])
            sheet['J'+str(numOfRow)] = time
            numOfRow +=1
            
    xfile.save('text.xlsx')

main()