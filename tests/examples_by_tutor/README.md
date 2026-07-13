# List of Samples

This document lists the available samples organized by the type of constrains placed on calling the method.

## The precondition is always false

* `List.of#add`

## Certain argument values are excluded in absolute terms

* `List#containsAll` (`null`)

## Certain argument values are excluded in relation to object state

* `Throwable#initCause` (`this`)
* `ArrayList#toArray` (types must be compatible)
* `List#get` (index invalid)

## The object must have a intrinsic state (checkable by a regular language)

* `Throwable#initCause`
* `OutputStream#write`

## The object must have a intrinsic state (NOT checkable by a regular language)

* `ListIterator#next`
* `ListIterator#previous`
* `ListIterator#remove`
