#pip install openpyxl
#put this file and the the results.xlsx in the same folder
#output will be in the file text.xlsx
#for some reason this openpyxl module has some internal error but this does not affect the data.
#Thus I copy the column in the test.xlsx and copy that column to results.xlsx in excel.
from openpyxl import load_workbook



def main():
    numOfRow=2;
    xfile = load_workbook('Equivalence2to3.xlsx')
    #sheet = xfile.get_sheet_by_name('Emptiness-complete')
    sheet = xfile.active
    
    with open('EquivalenceOf2to3Excel.txt') as f:
        for line in f:
            #get time from file
            line = line.split()
            combinations = line[0]
            sumTime= int(line[1])
            multDigit = int(line[2])
            sfaTime = int(line[3])
            reverseSafa = int(line[4])
            safaFull = int(line[5])
            safaSolver = int(line[6])
            safaSub = int(line[7])
            sfaMinusSafa = int(line[8])
            reverseMinusSafaFull = int(line[9])
           

            sheet['A'+str(numOfRow)] = combinations
            sheet['B'+str(numOfRow)] = sumTime
            sheet['C'+str(numOfRow)] = multDigit
            sheet['D'+str(numOfRow)] = sfaTime
            sheet['E'+str(numOfRow)] = reverseSafa
            sheet['F'+str(numOfRow)] = safaFull
            sheet['G'+str(numOfRow)] = safaSolver
            sheet['H'+str(numOfRow)] = safaSub
            sheet['I'+str(numOfRow)] = sfaMinusSafa
            sheet['J'+str(numOfRow)] = reverseMinusSafaFull

            numOfRow +=1
            
    xfile.save('text.xlsx')

main()