package rs.pedjaapps.smc.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import rs.pedjaapps.smc.utility.Utility;

public class Sprite extends GameObject
{
    private boolean rotationAplied = false;
    public String textureAtlas;
    public String textureName;//name of texture from pack or png
    public Type type = null;
    public Rectangle mOrigDrawRect;
    Texture txt = null;
    TextureRegion region = null;

    public Sprite(World world, Vector2 size, Vector3 position, Rectangle colRect)
    {
        super(world, size, position);
        this.position = position;
        mOrigDrawRect = World.RECT_POOL.obtain();
        mOrigDrawRect.set(mDrawRect);
        if (colRect != null)
        {
            mColRect.x = mDrawRect.x + Math.abs(colRect.x);
            mColRect.y = mDrawRect.y + Math.abs(colRect.y);
            mColRect.width = colRect.width;
            mColRect.height = colRect.height;
        }
    }

    @Override
    public void _render(SpriteBatch spriteBatch)
    {
        if (txt != null || region != null)
        {
            float width = txt == null ? Utility.getWidth(region, mOrigDrawRect.height) : Utility.getWidth(txt, mOrigDrawRect.height);
            float originX = width * 0.5f;
            float originY = getOriginY();
            float rotation = mRotationZ;
            boolean flipX = mRotationY == 180;
            boolean flipY = mRotationX == 180;

            if (txt != null)
            {
                spriteBatch.draw(txt, mOrigDrawRect.x, mOrigDrawRect.y, originX, originY, width, mOrigDrawRect.height, 1, 1, rotation, 0, 0, txt.getWidth(), txt.getHeight(), flipX, flipY);
            }
            else
            {
                region.flip(flipX, flipY);//flip it
                spriteBatch.draw(region, mOrigDrawRect.x, mOrigDrawRect.y, originX, originY, width, mOrigDrawRect.height, 1, 1, rotation);
                region.flip(flipX, flipY);//return it to original
            }

        }
        else
        {
            throw new IllegalStateException("both Texture and TextureRegion are null");
        }
        //debug
        //mRotationZ = mRotationZ + 0.1f;
        //if(mRotationZ > 360)mRotationZ = 0;
        //applyRotation();
        //debug end
    }

    @Override
    public void _update(float delta)
    {

    }

    @Override
    public void initAssets()
    {
        //mRotationX = mRotationY = mRotationZ = 0;
        if(mRotationZ == 90 && mRotationX == 0 && mRotationY == 0)
        {
            mRotationY = 180;
            mRotationX = 180;
        }
        //load all assets
        TextureAtlas atlas = null;
        if (textureAtlas != null && textureAtlas.length() > 0)
        {
            atlas = world.screen.game.assets.manager.get(textureAtlas);
        }

        if (atlas != null)
        {
            region = atlas.findRegion(textureName.split(":")[1]);
        }
        else
        {
            txt = world.screen.game.assets.manager.get(textureName);
        }
        if(!rotationAplied)
        {
            applyRotation();
            rotationAplied = true;
        }
        if(mDrawRect.width == 0)
        {
            float width;
            if(region == null)
            {
                width = Utility.getWidth(txt, mDrawRect.height);
            }
            else
            {
                width = Utility.getWidth(region, mDrawRect.height);
            }
            mDrawRect.width = width;
            updateBounds();
        }

    }

    @Override
    public void dispose()
    {
        super.dispose();
        txt = null;
        region = null;
        World.RECT_POOL.free(mOrigDrawRect);
        world.SPRITE_POOL.free(this);
    }

    private void applyRotation()
    {
        //apply rotation
        if (mRotationX == 180.0)
        {
            mColRect.y = mDrawRect.y + ((mDrawRect.y + mDrawRect.height) - (mColRect.y + mColRect.height));
        }

        if (mRotationY == 180.0)
        {
            mColRect.x = mDrawRect.x + ((mDrawRect.x + mDrawRect.width) - (mColRect.x + mColRect.width));
        }

        /*if (mRotationZ == 90.0f)
        {
            // rotate position
            Vector2 pos = rotate(mDrawRect, mDrawRect.width * 0.5f, mDrawRect.height * 0.5f, mRotationZ);
            mDrawRect.x = pos.x;
            mDrawRect.y = pos.y - mDrawRect.width;

            // rotate collision position
            Vector2 colPos = rotate(mColRect, mColRect.width * 0.5f, mColRect.height * 0.5f, mRotationZ);
            mColRect.x = colPos.x;
            mColRect.y = colPos.y - mColRect.width;

            // switch width and height
            float orig_w = mDrawRect.width;
            mDrawRect.width = mDrawRect.height;
            mDrawRect.height = orig_w;
            // switch collision width and height
            float orig_col_w = mColRect.width;
            mColRect.width = mColRect.height;
            mColRect.height = orig_col_w;
            World.VECTOR2_POOL.free(pos);
            World.VECTOR2_POOL.free(colPos);
        }
        // mirror
        else if (mRotationZ == 180.0f)
        {
            mColRect.x = mDrawRect.width - (mColRect.width + mColRect.x);
            mColRect.y = mDrawRect.height - (mColRect.height + mColRect.y);
        }
        else if (mRotationZ == 270.0f)
        {
            // rotate position
            Vector2 pos = rotate(mDrawRect, mDrawRect.width * 0.5f, mDrawRect.height * 0.5f, mRotationZ);
            mDrawRect.x = pos.x;
            mDrawRect.y = pos.y;
            // rotate collision position
            Vector2 colPos = rotate(mColRect, mColRect.width * 0.5f, mColRect.height * 0.5f, mRotationZ);
            mColRect.x = colPos.x;
            mColRect.y = colPos.y;

            // switch width and height
            float orig_w = mDrawRect.width;
            mDrawRect.width = mDrawRect.height;
            mDrawRect.height = orig_w;
            // switch collision width and height
            float orig_col_w = mColRect.width;
            mColRect.width = mColRect.height;
            mColRect.height = orig_col_w;
            World.VECTOR2_POOL.free(pos);
            World.VECTOR2_POOL.free(colPos);
        }
        position.x = mColRect.x;
        position.y = mColRect.y;*/
        if(mRotationZ != 0)
        {
            float originY = getOriginY();
            rotate2(mOrigDrawRect, mDrawRect, mOrigDrawRect.width / 2, getOriginY(), mRotationZ);
            rotate2(mColRect, mColRect, mColRect.width / 2, originY, mRotationZ);
        }
    }

    private float getOriginY()
    {
        //TODO for some reason this is the only way that rotation works as expected
        //TODO need to take a deeper investigation into original code to determine how rotation should actually work
        float originY = 0;
        if(mDrawRect.width == mDrawRect.height)
        {
            originY = mOrigDrawRect.height / 2;
        }
        return originY;
    }

    /**
     * @param originX , originY, rotation point relative to self*/
    public void rotate2(Rectangle sourceRect, Rectangle destRect, float originX, float originY, float rotate)
    {
        float x = sourceRect.x;
        float y = sourceRect.y;
        float centerX = sourceRect.x + originX;
        float centerY = sourceRect.y + originY;
        float hW = sourceRect.width / 2;
        float hH = sourceRect.height / 2;
        float w = sourceRect.width;
        float h = sourceRect.height;

        Polygon polygon = new Polygon(new float[]{
                /*centerX - hW, centerY - hH,
                centerX - hW, centerY + hH,
                centerX + hW, centerY + hH,
                centerX + hW, centerY - hH*/

                x, y,
                x, y + h,
                x + w, y + h,
                x + w, y
        });

        polygon.setOrigin(centerX, centerY);
        polygon.setRotation(rotate);
        destRect.set(polygon.getBoundingRectangle());
    }

    public Vector2 rotate(Rectangle rect, float originX, float originY, float rotation)
    {
        /*
        degree = 90
        x = 4
        y = 4
        width = 2
        height = 1
        ox = width / 2			//relative center x
        oy = height / 2         //relative center y
        cx = x + ox		//world center x
        cy = y + ox		//world center y

        rx = cx + (ox * cos(degree)) - (oy * sin(degree))
        ry = cy + (ox * sin(degree)) + (oy * cos(degree))
        */
        Vector2 r = World.VECTOR2_POOL.obtain();

        float cx = rect.x + originX;
        float cy = rect.y + originY;

        r.x = cx + (originX * MathUtils.cosDeg(rotation)) - (originY * MathUtils.sinDeg(rotation));
        r.y = cy + (originX * MathUtils.sinDeg(rotation)) + (originY * MathUtils.cosDeg(rotation));
        return r;
    }

    /**
     * Type of the block
     * massive = player cant pass by it
     * passive = player passes in front of it
     * front_passive = player passes behind it
     */
    public enum Type
    {
        massive, passive, front_passive, halfmassive, climbable
    }

    @Override
    public String toString()
    {
        return "Sprite{" +
                "\nrotationAplied=" + rotationAplied +
                "\n textureAtlas='" + textureAtlas + '\'' +
                "\n textureName='" + textureName + '\'' +
                "\n type=" + type +
                "\n mOrigDrawRect=" + mOrigDrawRect +
                "\n txt=" + txt +
                "\n region=" + region +
                "\n} \n" + super.toString();
    }
}
