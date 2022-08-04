package man.ui

class CVTagLib {
    static defaultEncodeAs = [taglib:'none']
    static namespace = 'cv'

    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def tt = { attrs, body ->
        out << """
        <span class="cvtooltip">
            <span class="tooltipIcon">?</span>
            <span class='ttspan'>
                ${body} 
            </span>
        </span>
        """
    }
}
