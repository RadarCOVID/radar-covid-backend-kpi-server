import boto3
from moto import mock_s3
import logging
import lambda_function

@mock_s3
def test_lambda():
    s3 = boto3.client('s3', region_name='us-east-1')
    # We need to create the bucket since this is all in Moto's 'virtual' AWS account
    s3.create_bucket(Bucket='mybucket')

    # Upload csv files to mock s3 bucket
    s3.upload_file('./test/androidDownloads.csv', 'mybucket', 'stats_installs_installs_es.gob.radarcovid.csv')
    s3.upload_file('./test/iosDownloads.csv', 'mybucket', 'radar_covid-unidades_de_app.csv')

    # Android event
    event = {
         "Records":[
            {"s3":{
                  "bucket":{
                     "name":"mybucket"
                  },
                  "object":{
                     "key":"stats_installs_installs_es.gob.radarcovid.csv"
                  }
               }
            }
         ]
      }
    lambda_function.lambda_handler(event, [])

    # iOS event
    event = {
         "Records":[
            {"s3":{
                  "bucket":{
                     "name":"mybucket"
                  },
                  "object":{
                     "key":"radar_covid-unidades_de_app.csv"
                  }
               }
            }
         ]
      }
    lambda_function.lambda_handler(event, [])

if __name__ == '__main__':
    logger = logging.getLogger()
    logger.addHandler(logging.StreamHandler())
    test_lambda()

