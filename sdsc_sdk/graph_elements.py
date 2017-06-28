"""Elements of the knowledge graph"""


class Vertex(object):
    """A vertex"""

    def __init__(self, _id=None, types=set(), properties=list(), **kwargs):
        self.id = _id
        self.types = types
        self.properties = dict([(p['key'], MultiPropertyValue(**p)) for p in properties])
        for k, v in kwargs.items():
            self.__dict__[k] = v

    def __repr__(self):
        return 'Vertex(id={id}, types={types}, properties={properties})'.format(**self.__dict__)

    def __str__(self):
        return self.__repr__()


class MultiPropertyValue(object):
    """Holds multiple values for a single key"""

    def __init__(self, key=None, data_type=None, cardinality=None, values=list(), **kwargs):
        self.key = key
        self.data_type = data_type
        self.cardinality = cardinality
        self.values = values
        for k, v in kwargs.items():
            self.__dict__[k] = v

    def __repr__(self):
        return 'MultiPropertyValue(key={key}, data_type={data_type}, cardinality={cardinality}, values={values})'.format(**self.__dict__)

    def __str__(self):
        return self.__repr__()
