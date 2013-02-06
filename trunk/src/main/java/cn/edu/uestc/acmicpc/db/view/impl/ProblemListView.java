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

import cn.edu.uestc.acmicpc.db.entity.Problem;
import cn.edu.uestc.acmicpc.db.entity.ProblemTag;
import cn.edu.uestc.acmicpc.db.view.base.View;
import cn.edu.uestc.acmicpc.util.annotation.Ignored;
import cn.edu.uestc.acmicpc.util.exception.AppException;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * description
 *
 * @author <a href="mailto:muziriyun@gmail.com">mzry1992</a>
 * @version 2
 */
@SuppressWarnings("UnusedDeclaration")
public class ProblemListView extends View<Problem> {

    private Integer problemId;
    private String title;
    private String source;
    private Integer solved;
    private Integer tried;
    private Boolean isSPJ;
    private Boolean isVisible;
    private Integer difficulty;
    private List<String> tags;

    /**
     * Get ProblemListView entity by problem entity.
     *
     * @param problem specific problem entity
     * @throws cn.edu.uestc.acmicpc.util.exception.AppException
     */
    public ProblemListView(Problem problem) throws AppException {
        super(problem);
        List<String> list = new LinkedList<>();
        Collection<ProblemTag> problemTags = problem.getProblemtagsByProblemId();
        for (ProblemTag problemTag : problemTags) {
            list.add(problemTag.getTagByTagId().getName());
        }
        setTags(list);
    }

    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getSolved() {
        return solved;
    }

    public void setSolved(Integer solved) {
        this.solved = solved;
    }

    public Integer getTried() {
        return tried;
    }

    public void setTried(Integer tried) {
        this.tried = tried;
    }

    public Boolean getIsSpj() {
        return isSPJ;
    }

    public void setIsSpj(Boolean SPJ) {
        isSPJ = SPJ;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getTags() {
        return tags;
    }

    @Ignored
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}