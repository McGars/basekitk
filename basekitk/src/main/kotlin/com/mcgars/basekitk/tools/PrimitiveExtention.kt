package com.mcgars.basekitk.tools


operator fun Int?.compareTo(other: Int?): Int {
    if (this == null || other == null) return -1
    return this.compareTo(other)
}