package cz.lsrom.tvmanager.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by lsrom on 1.4.2017.
 */
public class HashUtilsTest {

    @Test
    public void testSha256NullParameter (){
        assertTrue(HashUtils.sha256(null) == null);
    }

    @Test
    public void testSha256EmptyStringNotEmptyResult (){
        assertTrue(HashUtils.sha256("".getBytes()).length > 0);
    }

    @Test
    public void testSha256HashOfEmptyString (){
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", HashUtils.bytesToHex(HashUtils.sha256("".getBytes())));
    }

    @Test
    public void testSha256OfSimpleString (){
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", HashUtils.bytesToHex(HashUtils.sha256("abc".getBytes())));
    }

    @Test
    public void testSha256OfComplexString (){
        assertEquals("e647c6f5ff4349a03eec767638f6cfe06d7648186e61e10872d37c6e71c34aa1", HashUtils.bytesToHex(HashUtils.sha256(("\n" +
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In ultrices nisl arcu, quis commodo ante blandit sit amet. Aliquam erat volutpat. Aenean faucibus massa vitae nibh suscipit, sed sollicitudin tellus ullamcorper. Donec tincidunt massa imperdiet, pulvinar eros eget, tempor diam. Curabitur a fringilla augue. Duis pulvinar condimentum libero. Ut egestas in nulla quis rhoncus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi et felis sed dui malesuada malesuada in at eros. Ut placerat ac odio a volutpat. Sed vel ligula non nulla lacinia pharetra.\n" +
                "\n" +
                "Etiam mattis a nisi vel volutpat. Sed ut felis eget felis mattis viverra. Quisque commodo posuere feugiat. Fusce vitae nisl ex. Ut quis dictum nisl. Integer dictum eu ipsum vitae tincidunt. Maecenas pharetra quam vitae velit facilisis sagittis. Nam metus sapien, fringilla a leo vestibulum, venenatis blandit lorem.\n" +
                "\n" +
                "Aliquam ultricies dictum enim, facilisis malesuada justo pulvinar at. Aliquam et elit et arcu dignissim pharetra quis nec nisl. Quisque quis aliquam ante. Sed placerat commodo felis. Quisque sed velit consectetur, viverra lacus ac, ornare sapien. Aenean hendrerit metus venenatis placerat varius. Vivamus at dictum felis. Donec sed justo odio. In dapibus ex enim, in convallis augue gravida eget. In hac habitasse platea dictumst. Maecenas ac tortor aliquam, vestibulum felis lacinia, tincidunt enim. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
                "\n" +
                "Aliquam mattis mattis condimentum. Mauris lectus ipsum, accumsan aliquam magna et, tristique feugiat diam. Phasellus pharetra porttitor molestie. Integer sit amet pellentesque leo. Pellentesque ornare diam vitae fringilla malesuada. Ut molestie posuere magna et imperdiet. In magna arcu, euismod quis accumsan tempus, hendrerit ut tellus. Sed eget bibendum velit. Nunc ullamcorper dui ac massa auctor semper. Donec vehicula ultricies mi at viverra.\n" +
                "\n" +
                "In orci neque, porttitor et purus sed, finibus porta mi. Vestibulum id nibh dolor. Sed elementum efficitur arcu ut eleifend. Nullam gravida id enim sit amet fringilla. Duis fringilla eleifend est quis consequat. Maecenas feugiat vulputate laoreet. Nunc justo justo, ornare ac aliquet vel, luctus ac lorem. Ut fermentum hendrerit erat eget pellentesque. Duis interdum vulputate ex id lobortis. Proin magna tortor, rutrum ullamcorper dui sit amet, suscipit malesuada est. Integer purus libero, fermentum quis quam ac, eleifend rutrum enim. Ut non sem sem. Nulla a mi fringilla, fermentum massa eget, eleifend elit. Curabitur luctus sapien tortor, facilisis imperdiet nisi commodo id.").getBytes())));
    }
}
