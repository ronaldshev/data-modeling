import csv
import json
import time
import re

with open("cast.csv", 'r', newline='\n', encoding='utf-8') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    outputFile = open('castparsed.csv', 'w')
    output = csv.writer(outputFile, lineterminator='\n')
    count = 0
    listOfKeywords = []
    for row in reader:
        single_json = row[0]
        single_json = re.sub('order": 5}(.+)', r'order": 5}]', single_json)
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
            if name['order'] == '5':
                break;
            for keywordid in listOfKeywords:
                if(keywordid == name['id']):
                    insideArray = True
            if(insideArray == False):
                listOfKeywords.append(name['id'])
                output.writerow(name.values())
                print(name['name'], name['character'], name['order'])
