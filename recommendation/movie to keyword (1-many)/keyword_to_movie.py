import csv
import json
import time

with open("moviesnkeywords.csv", 'r', newline='\n', encoding='utf-8') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    outputFile = open('keywordsMatchesMovie.csv', 'w')
    output = csv.writer(outputFile, lineterminator='\n')
    count = 0
    listOfKeywords = []
    for row in reader:
        single_json = row[0]
        single_json = single_json.replace("\\", "\\\\")
        single_json = single_json.replace("\\\"", "\\\\\"")
        data = json.loads(single_json)
        for name in data:
            name['movieid'] = row[2]
            insideArray = False
            if count == 0:
                header = name.keys()
                output.writerow(header)
                count += 1
            output.writerow(name.values())