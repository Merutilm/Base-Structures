package kr.merutilm.base.struct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public record ImageFile(@Nonnull String name) implements Struct<ImageFile> {

    @Nullable
    public static ImageFile convert(String value) {
        if (value == null) {
            return null;
        }
        return new ImageFile(value);
    }

    @Override
    public Builder edit() {
        return new Builder(name);
    }

    public static final class Builder implements StructBuilder<ImageFile> {
        private String name;

        public Builder(@Nonnull String name) {
            this.name = name;
        }


        public Builder setName(@Nonnull String name) {
            this.name = name;
            return this;
        }

        @Override
        public ImageFile build() {
            return new ImageFile(name);
        }

    }
    @Nullable
    public BufferedImage getImage(String parent) throws IOException {
        File file = getFile(parent);
        return file == null ? null : ImageIO.read(file);
    }
    public File getFile(String parent){
        File file = new File(parent, toString().replace("\\\\", "/"));
        if(!file.exists() || file.isDirectory()){
            return null;
        }
        return file;
    }

    @Nonnull
    @Override
    public String toString() {
        if(name.isEmpty()){
            return "";
        }
        return name;
    }
}
