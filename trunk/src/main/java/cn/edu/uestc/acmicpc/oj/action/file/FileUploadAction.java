/*
 *
 *  * cdoj, UESTC ACMICPC Online Judge
 *  * Copyright (c) 2013 fish <@link lyhypacm@gmail.com>,
 *  * 	mzry1992 <@link muziriyun@gmail.com>
 *  *
 *  * This program is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU General Public License
 *  * as published by the Free Software Foundation; either version 2
 *  * of the License, or (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program; if not, write to the Free Software
 *  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package cn.edu.uestc.acmicpc.oj.action.file;

import cn.edu.uestc.acmicpc.oj.action.BaseAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Action for file upload service
 *
 * @author <a href="mailto:lyhypacm@gmail.com">fish</a>
 * @version 4
 */
public class FileUploadAction extends BaseAction {

    private List<File> uploadFile;
    private List<String> uploadFileContentType;
    private List<String> uploadFileFileName;

    private String savePath;

    public List<File> getUploadFile() {
        return uploadFile;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUploadFile(List<File> uploadFile) {
        this.uploadFile = uploadFile;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getUploadFileContentType() {
        return uploadFileContentType;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUploadFileContentType(List<String> uploadFileContentType) {
        this.uploadFileContentType = uploadFileContentType;
    }

    public List<String> getUploadFileFileName() {
        return uploadFileFileName;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUploadFileFileName(List<String> uploadFileFileName) {
        this.uploadFileFileName = uploadFileFileName;
    }

    public String getSavePath() {
        return servletContext.getRealPath(savePath);
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /**
     * Upload files.
     * <p/>
     * We should set ourselves' type filter for this upload action.
     *
     * @throws Exception
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void uploadFile() throws Exception {
        // TODO check type and size
        File dir = new File(getSavePath());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        List<File> files = getUploadFile();
        for (int i = 0; i < files.size(); i++) {
            FileOutputStream fos = new FileOutputStream(getSavePath() + "//" + getUploadFileFileName().get(i));
            FileInputStream fis = new FileInputStream(getUploadFile().get(i));
            byte[] buffers = new byte[1024];
            int len;
            while ((len = fis.read(buffers)) != -1) {
                fos.write(buffers, 0, len);
            }
        }
    }
}
