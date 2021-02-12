Description:
Provides service for updating the download data by device.
When CSV file upload with bucket S3, lambda function is executed and it's upload data on DB.

Environment variables:

DB_SCHEMA       -> KPI database schema
DB_HOST         -> database host
DB_PORT         -> database port
DB_USER         -> database kpi user
DB_PASS         -> database kpi password
DB_NAME         -> database name
IOS_FILE        -> iOS filename
ANDROID_FILE    -> android filename
PYTHON_LOGLEVEL -> loglevel

Triggers:

S3 -> PUT action.

Test:

There is a test file to do an upload files test. Boto3 and Moto3 dependencies are used to mock s3 bucket.