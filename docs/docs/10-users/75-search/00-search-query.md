# Search Query

<!--
This file was first generated and then modified. Generate a new version
from inside renku-data-services repository via this python script:

    > import renku_data_services.search.query_manual as qm
    > qm.manual_to_file("query-manual.mmd")

-->

The search accepts queries as a query string. A query may contain
specific and unspecific search terms.


## Query String

A query is a sequence of words. All words that are not recognized as
specific search terms are used for searching in various entity
properties, such as `name` or `description`. Specific search terms are
matched against a certain field. Terms are separated by whitespace.

Example:
```
numpy flight visibility:public
```

Searches for entities containing `numpy` _and_ `flight` that are
public.

The term order is usually not relevant, it may influence the score of
a result, though.

If a value for a specific field contains whitespace, quotes or a comma
it must be enclosed in quotes. Additionally, multiple values can be
provided for each field by using a comma separated list. The values
are treated as alternatives, so any such value would yield a result.

Example:
```
numpy flight visibility:public,private
```

Searches for entities containing `numpy` _and_ `flight` that are
_either_ `public` _or_ `private`.

## Fields

The following fields are available:

- id
- name
- slug
- visibility
- created
- createdby
- type
- role
- keyword
- namespace
- direct_member
- inherited_member
- doi
- publisher_name

Each field allows to specify one or more values, separated by comma.
The value must be separated by a `:`. For date fields, additional `<`
and `>` is supported.

## EntityTypes

The field `type` allows to search for specific entity types. If it is
missing, all entity types are included in the result. Entity types are:

- Project
- User
- Group
- DataConnector

Example:

```
type:Project
```


## Roles

The field `role` allows to search for projects the current user has
the given role. Other entities are excluded from the results.

- owner
- viewer
- editor


## Visibility

The `visibility` field can be used to restrict to entities with a
certain visibility. Users have a default visibility of `public`.
Possible values are:

- public
- private


## Created By

Selects entities that were created by a specific user.

```
createdby:abc-id-123
```


Note that this field only accepts user-ids! I cannot (yet) resolve usernames.

## Keywords

Entities with certain keywords can be searched, where multiple keywords given
in one field term are combined via *OR* and multiple field-terms are combined
via *AND*. Keywords have to match exactly (no typos allowed).

```
keyword:data,ml keyword:health,disease
```

Searches for entities that have either `data` or `ml` *and* either `health` or
`disease` as keywords. If keywords contain whitespace or a comma, they must be
quoted as written above. If a keyword contains a quote character (`"`), it must
be prefixed by a backslash (`\`) to escape from interpreting it as an
end-of-value symbol.

```
keyword:"data science",ml,"tl,dr","\"well\" said"
```


## Members

There are two fields that allow to search for entities where a given
user is either a member of. There are two variants:

- direct_member
- inherited_member

The first includes only entities where the given user has a direct
relationship. The latter additionally includes entities, where this
relationship is deduced from where the entity is located. For example,
a member of a group is also a member of all projects in that group. So
it would select all entities that are "somehow" related to that
person.

The value of that field is either a user id or a user name. Usernames
must be prefixed with an `@` and user ids can be specified as is.

Examples:

- `direct_member:@john.doe`
- `direct_member:abc-123-xyz`
- `inherited_member:@john.doe`
- `inherited_member:abc-123-xyz`

Multiple members can be specified, they will then match entities where
*all* these users are members. For these fields, specifying multiple
members in one field or multiple fields is equivalent.

`direct_member:@john.doe,abc-123`
is the same as `direct_member:@john.doe direct_member:abc-123`.

There is a hard limit on the number of members that can be specified.
This applies to both fields. If there are more specified, they will be
silently ignored.

Currently this limit is 4.


## DOIs

These are applicable only for data connectors created from DOIs.
The DOI value provided is not changed at all by the query parser.
Note that we store all DOI values not as full URLs
(i.e. `https://www.doi.org/10.16904/envidat.714`) but
only as the value (i.e. `10.16904/envidat.714`). Future improvements
will probably remove these limitations and handle full URLs transparently.
The search will look up this term in a case-insensitive manner.


```
doi:10.16904/envidat.714
```

## Publisher names

These are applicable only for data connectors created from DOIs.
The search will look up this term in a case-insensitive manner.

```
publisher_name:EnviDat
```

## Dates

Date fields, like

- created

accept date strings which can be specified in various ways. There are

- relative dates: `today`
- partial timestamps: `2023-05`, `2023-11-12T10`
- calculations based on the above: `today-5d`, `2023-10-15/10d`


### Relative dates

There are the following keywords for relative dates:

- today
- yesterday


### Partial Timestamps

Timestamps must be in ISO8601 form and are UTC based and allow to
specify time up to seconds. The full form is

```
yyyy-mm-ddTHH:MM:ssZ
```

Any part starting from right can be omitted. When querying, it will be
filled with either the maximum or minimum possible value depending on
the side of comparison. When the date is an upper bound, the missing
parts will be set to their minimum values. Conversely, when used as a
lower bound then the parts are set to its maximum value.

Example:

- `created>2023-03` will turn into `created>2023-03-31T23:59:59`
- `created<2023-03` will turn into `created<2023-03-01T00:00:00`


### Date calculations

At last, a date can be specified by adding or subtracting days from a
reference date. The reference date must be given either as a relative
date or partial timestamp. Then a `+`, `-` or `/` follows with the
amount of days.

The `/` character allows to add and subtract the days from the
reference date, making the reference date the middle.

Example:

- `created>today-14d` things created from 14 days ago
- `created<2023-05/14d` things created from last two weeks of April and first two weeks of May


### Date Comparison

Comparing dates with `>` and `<` is done as expected. More interesting
is to specify more than one date and the use of the `:` comparison.

The `:` can be used to specify ranges more succinctly. For a full
timestamp, it means *equals*. With partial timestamps it searches
within the minimum and maximum possible date for that partial
timestamp.

Since multiple values are combined using `OR`, it is possible to
search in multiple ranges.

Example:

```
created:2023-03,2023-06
```


The above means to match entities created in March 2023 or June 2023.

## Sorting

The query allows to define terms for sorting. Sorting is limited to
specific fields, which are:

- name
- created
- score

Sorting by a field is defined by writing the field name, followed by a
dash and the sort direction. Multiple such definitions are not yet
supported.

Example:

```
sort:score-desc
```
