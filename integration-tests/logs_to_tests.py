import collections
import os
import sys
import urlparse

import yaml

host = "http://localhost:8080"
basepath = "/TNAtoolAPI-Webapp/queries"

def parse_qs(q):
    qp = urlparse.parse_qs(q)
    for k,v in qp.items():
        qp[k] = unwrap(v)
    qp.pop('username', None)
    return qp

def unwrap(value):
    value = set(value)
    if len(value) == 1:
        value = value.pop()
    else:
        value = list(value)
    return value

tests = collections.defaultdict(set)
with open(sys.argv[1]) as f:
    for row in f:
        row = row.replace('"', '').split(" ")
        verb = row[5]
        url = host + row[6]
        code = row[8]
        refer = row[10].replace(host, '')
        if code != '200':
            continue
        u = urlparse.urlparse(url)
        if not u.path.startswith(basepath):
            continue
        tests[u.path].add((row[6], refer))


for k,v in sorted(tests.items()):
    filename = '-'.join(k.split('/')[2:])+'.yaml'

    config = [{
        'config': {
            'testset': 'API tests for %s'%(k),
            'timeout': 600
        }
    }]
    for q,r in sorted(v):
        qp = parse_qs(q)
        qp.pop('dbindex', None)
        config.append({
            'test': {
                'referer': r,
                'url': q
            }
        })
    print "\n\n\n\n"
    print filename
    print yaml.dump(config, default_flow_style=False)
    with open(os.path.join('api-tests',filename), 'wb') as f:
        f.write('---\n'+yaml.dump(config, default_flow_style=False))
