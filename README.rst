==============================================
Enhydra JaWE (a.k.a. Together Workflow Editor)
==============================================

Enhydra JaWE is an Open Source workflow editor that operates on XPDL files.

This repository is a git mirror of the Subversion repository on
SourceForge: https://sourceforge.net/projects/jawe/

This mirror has been created with the following sequence of commands::

    echo "sasaboy = Sasa Bojanic <sasaboy@together.at>" > authors-transform.txt
    git svn init --trunk=trunk --tags=tags --tags=tags/releases --branches=branches \
                 svn://svn.code.sf.net/p/jawe/code/
    git config svn.authorsfile authors-transform.txt
    git svn fetch
    
    git remote add origin git@github.com:Shoobx/JaWE.git
    
    # Create release branches pointing to the SVN ones
    git branch -r | grep origin/twe | sed -e 's#.*/##' | while read br; do echo git switch $br; done
    
    # Create tags
    git for-each-ref --format="%(refname:short) %(objectname)" refs/remotes/origin/tags \
    |  cut -d / -f 3- | while read ref; do git tag $ref; done
    git tag -d releases
    
    git push origin --all
    git push origin --tags
