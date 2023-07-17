package com.lifehopehealthapp.utils.timeLine.model

import android.os.Parcel
import android.os.Parcelable
import kotlin.properties.Delegates.observable

@TimelineAttributes.Parcelize
class TimelineAttributes(
        var markerSize: Int,
        var markerColor: Int,
        var markerInCenter: Boolean,
        var markerLeftPadding: Int,
        var markerTopPadding: Int,
        var markerRightPadding: Int,
        var markerBottomPadding: Int,
        var linePadding: Int,
        var lineWidth: Int,
        var startLineColor: Int,
        var endLineColor: Int,
        var lineStyle: Int,
        var lineDashWidth: Int,
        var lineDashGap: Int
): Parcelable {
    annotation class Parcelize

    @IgnoredOnParcel
    var orientation by observable(Orientation.VERTICAL) { _, oldValue, newValue ->
        onOrientationChanged?.invoke(oldValue, newValue)
    }

    @IgnoredOnParcel
    var onOrientationChanged: ((Orientation, Orientation) -> Unit)? = null

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {

    }

    fun copy(): TimelineAttributes {
        val attributes = TimelineAttributes(markerSize, markerColor, markerInCenter, markerLeftPadding, markerTopPadding,
                markerRightPadding, markerBottomPadding, linePadding, lineWidth, startLineColor, endLineColor, lineStyle, lineDashWidth,
                lineDashGap)
        attributes.orientation = orientation
        return attributes
    }

    override fun toString(): String {
        return "TimelineAttributes(markerSize=$markerSize, markerColor=$markerColor, markerInCenter=$markerInCenter, " +
                "markerTopPadding=$markerTopPadding, markerBottomPadding=$markerBottomPadding, linePadding=$linePadding, " +
                "lineWidth=$lineWidth, startLineColor=$startLineColor, endLineColor=$endLineColor, lineStyle=$lineStyle, " +
                "lineDashWidth=$lineDashWidth, lineDashGap=$lineDashGap, onOrientationChanged=$onOrientationChanged)"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(markerSize)
        parcel.writeInt(markerColor)
        parcel.writeByte(if (markerInCenter) 1 else 0)
        parcel.writeInt(markerLeftPadding)
        parcel.writeInt(markerTopPadding)
        parcel.writeInt(markerRightPadding)
        parcel.writeInt(markerBottomPadding)
        parcel.writeInt(linePadding)
        parcel.writeInt(lineWidth)
        parcel.writeInt(startLineColor)
        parcel.writeInt(endLineColor)
        parcel.writeInt(lineStyle)
        parcel.writeInt(lineDashWidth)
        parcel.writeInt(lineDashGap)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimelineAttributes> {
        override fun createFromParcel(parcel: Parcel): TimelineAttributes {
            return TimelineAttributes(parcel)
        }

        override fun newArray(size: Int): Array<TimelineAttributes?> {
            return arrayOfNulls(size)
        }
    }
}

annotation class IgnoredOnParcel
