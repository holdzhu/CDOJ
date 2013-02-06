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

package cn.edu.uestc.acmicpc.db.view.impl;

import cn.edu.uestc.acmicpc.db.entity.Contest;
import cn.edu.uestc.acmicpc.db.view.base.View;

import java.sql.Timestamp;

/**
 * @author <a href="mailto:lyhypacm@gmail.com">fish</a>
 * @version 2
 */
@SuppressWarnings("UnusedDeclaration")
public class ContestView extends View<Contest> {
    private Integer contestId;
    private String title;
    private String description;
    private Byte type;
    private Timestamp time;
    private Integer length;
    private Boolean isVisible;

    @SuppressWarnings("UnusedDeclaration")
    public Integer getContestId() {
        return contestId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    public String getTitle() {
        return title;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setTitle(String title) {
        this.title = title;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getDescription() {
        return description;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setDescription(String description) {
        this.description = description;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Byte getType() {
        return type;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setType(Byte type) {
        this.type = type;
    }

    public Timestamp getTime() {
        return time;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setTime(Timestamp time) {
        this.time = time;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Integer getLength() {
        return length;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLength(Integer length) {
        this.length = length;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Boolean getIsVisible() {
        return isVisible;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setIsVisible(Boolean visible) {
        isVisible = visible;
    }

    /**
     * Fetch data from entity.
     *
     * @param contest specific entity
     */
    @SuppressWarnings("UnusedDeclaration")
    public ContestView(Contest contest) {
        super(contest);
    }
}