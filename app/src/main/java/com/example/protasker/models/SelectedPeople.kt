package com.projemanag.model

import android.os.Parcel
import android.os.Parcelable

data class SelectedPeople(
    val id: String = "",
    val imageUri: String = ""
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(imageUri)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SelectedPeople> =
            object : Parcelable.Creator<SelectedPeople> {
                override fun createFromParcel(source: Parcel): SelectedPeople =
                    SelectedPeople(source)

                override fun newArray(size: Int): Array<SelectedPeople?> = arrayOfNulls(size)
            }
    }
}
