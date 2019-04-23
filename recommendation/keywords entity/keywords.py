import csv
import json
import time

with open("keyword.csv", 'r', newline='\n', encoding='utf-8') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    outputFile = open('keywords.csv', 'w')
    output = csv.writer(outputFile, lineterminator='\n')
    count = 0
    listOfKeywords = []
    for row in reader:
        single_json = row[0]
        single_json = single_json.replace("\\", "\\\\")
        single_json = single_json.replace("\\\"", "\\\\\"")
        data = json.loads(single_json)
        for name in data:
            insideArray = False
            if count == 0:
                header = name.keys()
                output.writerow(header)
                count += 1
            if count == 1:
                listOfKeywords.append(name['id'])
                count += 1
            for keywordid in listOfKeywords:
                if(keywordid == name['id']):
                    insideArray = True
            if(insideArray == False):
                listOfKeywords.append(name['id'])
                output.writerow(name.values())
                #print(json.dumps(data, indent=4))
