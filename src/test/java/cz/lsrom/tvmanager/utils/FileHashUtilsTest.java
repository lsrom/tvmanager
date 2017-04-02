package cz.lsrom.tvmanager.utils;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by lsrom on 2.4.2017.
 */
public class FileHashUtilsTest {
    @Test
    public void testNullFile (){
        assertTrue(FileHashUtils.hashFile(null) == null);
    }

    @Test
    public void testNotExistingFile (){
        File f = new File("g/x/y/Y/z/X/z/y/c/f/gh/h");   // this file shouldn't exist
        assertFalse(f.exists());
        assertTrue(FileHashUtils.hashFile(f) == null);
    }

    @Test
    public void testFileExistHash (){
        File f = new File("src/test/java/cz/lsrom/tvmanager/utils/files/test_file_1");
        assertTrue(f.exists());
        assertTrue(FileHashUtils.hashFile(f).length > 0);
    }

    @Test
    public void testTwoFileHashesEqualEvenWithDifferentFilename (){
        File f1 = new File("src/test/java/cz/lsrom/tvmanager/utils/files/test_file_1");
        File f2 = new File("src/test/java/cz/lsrom/tvmanager/utils/files/test_file_2");

        byte[] a1 = FileHashUtils.hashFile(f1);
        byte[] a2 = FileHashUtils.hashFile(f2);

        assertTrue(HashUtils.byteArraysEqual(a1, a2));
    }

    @Test
    public void testFileHashEqualsItselfHash (){
        File f1 = new File("src/test/java/cz/lsrom/tvmanager/utils/files/test_file_1");
        File f2 = new File("src/test/java/cz/lsrom/tvmanager/utils/files/test_file_1");

        byte[] a1 = FileHashUtils.hashFile(f1);
        byte[] a2 = FileHashUtils.hashFile(f2);

        assertTrue(HashUtils.byteArraysEqual(a1, a2));
    }

    @Test
    public void testHashOfEmptyFileIsNotEmpty (){
        File f = new File("src/test/java/cz/lsrom/tvmanager/utils/files/test_file_empty");
        byte[] a = FileHashUtils.hashFile(f);

        assertTrue(a.length > 0);
    }

    @Test
    public void testHashOfEmptyFileEqualsHashOfEmptyString (){
        File f = new File("src/test/java/cz/lsrom/tvmanager/utils/files/test_file_empty");
        byte[] a = FileHashUtils.hashFile(f);

        assertTrue(HashUtils.byteArraysEqual(a, HashUtils.sha256("".getBytes())));
    }
}
