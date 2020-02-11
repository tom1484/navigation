import json

file_name = 'Navigation/app/src/main/res/data/map.json'
with open(file_name) as json_file:
    data = json.load(json_file)

while True:
    path = input('path: ')
    if path == 'end':
        break
    # path = 'events/shelves'
    path = path.split('/')

    array = data
    for obj in path:
        array = array[obj]

    objFormat = array[0]
    # print(objFormat)

    obj = {}
    for key in objFormat.keys():
        value = input(f'{key}: ')

        valType = type(objFormat[key])
        if valType == int:
            value = int(value)
        elif valType == float:
            value = float(value)
        elif valType == list:
            value = value.split(',')
            value = [int(v) for v in value]

        obj[key] = value

    array.append(obj)
    print(obj)

with open(file_name, 'w') as json_file:
    data = json.dump(data, json_file)
