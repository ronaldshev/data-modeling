import csv
import json
import time
import re

with open("movie to cast.csv", 'r', newline='\n') as csvfile:
    reader = csv.reader(csvfile, delimiter=',', quotechar='"')
    outputFile = open('movieMatchesCast.csv', 'w')
    output = csv.writer(outputFile, lineterminator='\n')
    count = 0
    for row in reader:
        single_json = row[0]
        #single_json = re.sub('order": 5}(.+)', r'order": 5}]', single_json)
        single_json = single_json.replace("\\", "\\\\")
        single_json = single_json.replace("\\\"", "\\\\\"")
        data = json.loads(single_json)
        for name in data:
            name['movieid'] = row[1]
            if count == 0:
                header = name.keys()
                output.writerow(header)
                count += 1
            if name['order'] == '5':
                break;
            output.writerow(name.values())