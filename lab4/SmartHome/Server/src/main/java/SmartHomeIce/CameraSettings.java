//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.10
//
// <auto-generated>
//
// Generated from file `smarthome.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package SmartHomeIce;

public class CameraSettings implements java.lang.Cloneable,
                                       java.io.Serializable
{
    public float pan;

    public float tilt;

    public float zoom;

    public CameraSettings()
    {
    }

    public CameraSettings(float pan, float tilt, float zoom)
    {
        this.pan = pan;
        this.tilt = tilt;
        this.zoom = zoom;
    }

    public boolean equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        CameraSettings r = null;
        if(rhs instanceof CameraSettings)
        {
            r = (CameraSettings)rhs;
        }

        if(r != null)
        {
            if(this.pan != r.pan)
            {
                return false;
            }
            if(this.tilt != r.tilt)
            {
                return false;
            }
            if(this.zoom != r.zoom)
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int hashCode()
    {
        int h_ = 5381;
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, "::SmartHomeIce::CameraSettings");
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, pan);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, tilt);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, zoom);
        return h_;
    }

    public CameraSettings clone()
    {
        CameraSettings c = null;
        try
        {
            c = (CameraSettings)super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return c;
    }

    public void ice_writeMembers(com.zeroc.Ice.OutputStream ostr)
    {
        ostr.writeFloat(this.pan);
        ostr.writeFloat(this.tilt);
        ostr.writeFloat(this.zoom);
    }

    public void ice_readMembers(com.zeroc.Ice.InputStream istr)
    {
        this.pan = istr.readFloat();
        this.tilt = istr.readFloat();
        this.zoom = istr.readFloat();
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, CameraSettings v)
    {
        if(v == null)
        {
            _nullMarshalValue.ice_writeMembers(ostr);
        }
        else
        {
            v.ice_writeMembers(ostr);
        }
    }

    static public CameraSettings ice_read(com.zeroc.Ice.InputStream istr)
    {
        CameraSettings v = new CameraSettings();
        v.ice_readMembers(istr);
        return v;
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, java.util.Optional<CameraSettings> v)
    {
        if(v != null && v.isPresent())
        {
            ice_write(ostr, tag, v.get());
        }
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, CameraSettings v)
    {
        if(ostr.writeOptional(tag, com.zeroc.Ice.OptionalFormat.VSize))
        {
            ostr.writeSize(12);
            ice_write(ostr, v);
        }
    }

    static public java.util.Optional<CameraSettings> ice_read(com.zeroc.Ice.InputStream istr, int tag)
    {
        if(istr.readOptional(tag, com.zeroc.Ice.OptionalFormat.VSize))
        {
            istr.skipSize();
            return java.util.Optional.of(CameraSettings.ice_read(istr));
        }
        else
        {
            return java.util.Optional.empty();
        }
    }

    private static final CameraSettings _nullMarshalValue = new CameraSettings();

    /** @hidden */
    public static final long serialVersionUID = -1646338940L;
}
