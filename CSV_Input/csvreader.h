#ifndef CSVREADER_H
#define CSVREADER_H

#include <iostream>
#include <vector>
#include <string>
#include <fstream>
#include <sstream>
#include <iterator>

class CSVReader
{
public:
    CSVReader(std::ifstream file);

    std::istream& operator>>(std::istream& str, CSVReader& data)
    {
        data.readNextRow(str);
        return str;
    }

    std::string const& operator[](std::size_t index) const
    {
        return m_data[index];
    }
    std::size_t size() const;
    void readNextRow(std::istream& str);

private:
    std::vector<std::string>    m_data;
};

#endif // CSVREADER_H
