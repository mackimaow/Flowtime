package category

interface Isomorphism<T, K> {
    fun morph(obj: T): K
    fun morphInv(obj: K): T
}