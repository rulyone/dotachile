# Third-party notices

The email-rag toolkit depends on the following third-party software. None
of it is bundled into this repository — every dependency is fetched at
install time by `pip` (or, in the case of the embedding model, by
`sentence-transformers` from HuggingFace at runtime). This file is
provided for transparency, not as legal compliance: because we do not
redistribute these components in source or binary form, neither MIT nor
Apache 2.0 imposes an obligation to ship their license texts here.

If this toolkit is ever repackaged in a way that constitutes
redistribution (for example: a wheel published to PyPI, a Docker image,
or a `pyinstaller` binary), this file must be reviewed and the relevant
license texts and `NOTICE` files must be bundled.

## Direct dependencies

| Package | License | Project | How we use it |
|---|---|---|---|
| `presidio-analyzer` | MIT | <https://github.com/microsoft/presidio> | Statistical PII detection (Pass B of `redact.py`) |
| `rank-bm25` | Apache 2.0 | <https://github.com/dorianbrown/rank_bm25> | Lexical retrieval leg of `search.py` |
| `sentence-transformers` | Apache 2.0 | <https://github.com/UKPLab/sentence-transformers> | Semantic retrieval leg of `search.py` |
| `pytest` | MIT | <https://github.com/pytest-dev/pytest> | Test runner (dev only) |

## Models

| Model | License | Source | How we use it |
|---|---|---|---|
| `paraphrase-multilingual-mpnet-base-v2` | Apache 2.0 | <https://huggingface.co/sentence-transformers/paraphrase-multilingual-mpnet-base-v2> | Multilingual sentence embeddings for semantic search |
| `en_core_web_sm` (spaCy) | MIT | <https://spacy.io/models/en> | English NER for Presidio |
| `es_core_news_sm` (spaCy) | MIT | <https://spacy.io/models/es> | Spanish NER for Presidio |

## Python standard library

The Python standard library (`mailbox`, `html.parser`, `hmac`, `hashlib`,
`json`, `re`, `email`, `pathlib`, etc.) is licensed under the
[Python Software Foundation License](https://docs.python.org/3/license.html)
and is used as the runtime, not as a redistributed component.
