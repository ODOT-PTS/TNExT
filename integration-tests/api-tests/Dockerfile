FROM ubuntu:16.04
RUN apt-get update -y
RUN apt-get install -y python3 python3-pip python-pycurl libcurl4-openssl-dev openssl libssl-dev postgresql

ADD . /api-tests

RUN pip3 install -r /api-tests/requirements.txt

CMD ["/scripts/prep-db-and-run-tests.sh"]