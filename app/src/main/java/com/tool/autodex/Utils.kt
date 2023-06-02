package com.tool.autodex

import java.io.File

fun File.filterPath(filterString: String) = filterString.let { filter -> path.replace(filter, "") }

fun checkHeader(name: String) = name.startsWith(NAME_TAG_HEADER)