FROM ubuntu:16.04
RUN apt-get update -y
RUN apt-get install -y python3 python3-pip python-pycurl libcurl4-openssl-dev libcurl-devel openssl

ADD . /api-tests

RUN pip3 install -r /api-tests/requirements.txt

CMD ["resttest.py http://${TNAST_HOST} /api-tests/*.yml"]