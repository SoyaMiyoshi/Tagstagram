from keras.applications.resnet50 import ResNet50
from keras.preprocessing import image
from keras.applications.resnet50 import preprocess_input, decode_predictions
import numpy as np
import sys
import pymysql

if __name__ == "__main__":
    img_path = "src/main/resources/saved/" + sys.argv[1] + ".jpg"

    model = ResNet50(weights='imagenet')

    img = image.load_img(img_path, target_size=(224, 224))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)

    preds = model.predict(x)
    new_tag = decode_predictions(preds, top=1)[0][0][1]
    print(new_tag, sys.argv[1])

    connection = pymysql.connect(host="localhost", user="root", password="slslslsl", db="demodb3", charset="utf8mb4")

    try:
        with connection.cursor() as cursor:
            sql = "UPDATE uploaded SET tag=%s WHERE id=%s"
            cursor.execute(sql, (new_tag, sys.argv[1]))
            connection.commit()
    finally:
        connection.close()