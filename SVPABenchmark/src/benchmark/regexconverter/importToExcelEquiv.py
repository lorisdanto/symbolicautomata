#pip install openpyxl
#put this file and the the results.xlsx in the same folder
#output will be in the file text.xlsx
#for some reason this openpyxl module has some internal error but this does not affect the data.
#Thus I copy the column in the test.xlsx and copy that column to results.xlsx in excel.
from openpyxl import load_workbook



def main():
    numOfRow=2;
    xfile = load_workbook('EquivalenceConjunc2to3.xlsx')
    #sheet = xfile.get_sheet_by_name('Emptiness-complete')
    sheet = xfile.active
    
    with open('EquivalenceOf2to3Excel2.txt') as f:
        for line in f:
            #get time from file
            line = line.split()
            name = line[0]
            safa1= int(line[1])
            safa2 = int(line[2])
            sfa1 = int(line[3])
            sfa2 = int(line[4])
            safaFull = int(line[5])
            safaSolver = int(line[6])
            safaSub = int(line[7])
            sfa = int(line[8])
            sfaMinussafaFull=int(line[9])
            
           

            sheet['A'+str(numOfRow)] = name
            sheet['B'+str(numOfRow)] = safa1
            sheet['C'+str(numOfRow)] = safa2
            sheet['D'+str(numOfRow)] = sfa1
            sheet['E'+str(numOfRow)] = sfa2
            sheet['F'+str(numOfRow)] = safaFull
            sheet['G'+str(numOfRow)] = safaSolver
            sheet['H'+str(numOfRow)] = safaSub
            sheet['I'+str(numOfRow)] = sfa
            sheet['J'+str(numOfRow)] = sfaMinussafaFull

            numOfRow +=1
            
    xfile.save('text.xlsx')

main()