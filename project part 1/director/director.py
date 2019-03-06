import csv
import json
import time
import re

with open("director.csv", 'r', newline='\n', encoding='utf-8') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    outputFile = open('directors.csv', 'w')
    output = csv.writer(outputFile, lineterminator='\n')
    count = 0
    listOfDirectors = []
    for row in reader:
        single_json = row[0]
        single_json = re.sub('("Director"(.*?))\}(.*)', r'\1}]', single_json)
        single_json = single_json.replace("\\", "\\\\")
        single_json = single_json.replace("\\\"", "\\\\\"")
        data = json.loads(single_json)
        for name in data:
            del name['credit_id']
            del name['department']
            del name['gender']
            insideArray = False
            if count == 0:
                header = name.keys()
                output.writerow(header)
                count += 1
            if count == 1:
                listOfDirectors.append(name['id'])
                count += 1
            if name['job'] == "Director":
                for directorID in listOfDirectors:
                    if(directorID == name['id']):
                        insideArray = True
                if(insideArray == False):
                    del name['job']
                    listOfDirectors.append(name['id'])
                    output.writerow(name.values())
                    print(name['id'], name['name'])